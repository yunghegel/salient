package org.yunghegel.salient.editor.app

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.GLFormat
import ktx.app.clearScreen
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.gdx.utils.ext.clearColor
import org.yunghegel.gdx.utils.ext.clearDepth
import org.yunghegel.gdx.utils.ext.delta
import org.yunghegel.gdx.utils.ext.notnull
import org.yunghegel.salient.core.graphics.util.OutlineDepth
import org.yunghegel.salient.editor.app.configs.Settings
import org.yunghegel.salient.editor.input.ViewportController
import org.yunghegel.salient.editor.plugins.base.DefaultPlugin
import org.yunghegel.salient.editor.plugins.base.systems.registerHotkey
import org.yunghegel.salient.editor.plugins.gizmos.GizmoPlugin
import org.yunghegel.salient.editor.plugins.intersect.IntersectionPlugin
import org.yunghegel.salient.editor.plugins.outline.OutlinerPlugin
import org.yunghegel.salient.editor.plugins.outline.lib.Outliner
import org.yunghegel.salient.editor.plugins.picking.PickingPlugin
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.InterfaceInitializedEvent
import org.yunghegel.salient.engine.Pipeline
import org.yunghegel.salient.engine.State.*
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.api.plugin.Plugin
import org.yunghegel.salient.engine.api.tool.Tool
import org.yunghegel.salient.engine.api.undo.ActionHistoryKeyListener
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.lifecycle.EditorInitializedEvent
import org.yunghegel.salient.engine.events.lifecycle.WindowResizedEvent
import org.yunghegel.salient.engine.graphics.FBO
import org.yunghegel.salient.engine.graphics.GFX
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.scene3d.SceneContext
import org.yunghegel.salient.engine.system.*
import org.yunghegel.salient.engine.ui.UI
import kotlin.collections.set
import kotlin.reflect.KClass


/**
 * Application entry point. Responsible for building all resource and rendering pipelines and bootstrapping the editor context
 */

@Suppress("UNCHECKED_CAST")
class Salient : ApplicationAdapter() {


    val app = App()

    var gui: Gui by notnull()

    var scene: Scene by notnull()

    var project: Project by notnull()

    private var state: State by notnull()

    val pipeline: Pipeline = Companion


    val index = Index<Named>()

    data class PluginSet(val tools:MutableList<Tool>?,val systems:MutableList<System<Project,Scene>>?,val plugins:MutableList<Plugin>?)

    val registry : PluginSet
    lateinit var viewportController: ViewportController

    init {
        index.types[Tool::class.java] = mutableListOf()
        index.types[System::class.java] = mutableListOf()
        index.types[Plugin::class.java] = mutableListOf()
        registry = PluginSet(index.list(Tool::class.java)!! as MutableList<Tool>,index.list(System::class.java)!! as MutableList<System<Project,Scene>>,index.list(Plugin::class.java)!! as MutableList<Plugin>)
    }
    val outliner : OutlineDepth by lazy { inject() }
    override fun create() {

    /* allow asyncronous actions when we need it */
        async.init()
        run {
            singleton(this)
            singleton(index)
            singleton(app)
            singleton<Engine>(engine as Engine)
            singleton<Pipeline>(engine as Pipeline)
        }

        profile("init modules")
        {
            listOf(app, UI, GFX, Input).forEach { module ->
                module.initialize()
            }
        }

        val (project, scene) = app.bootstrap()
        this.project = project
        this.scene = scene

        initSystems()
        buildPipeline()
        configureUI()
        configureInput()

        listOf(
            DefaultPlugin(),
            PickingPlugin(),
            GizmoPlugin(),
            OutlinerPlugin(),
            IntersectionPlugin()
        ).onEach { plugin ->
            createPlugin(plugin)
        }

        Netgraph.add("tools") {
            index.types[Tool::class.java]?.filter { it is Tool && it.active }?.joinToString { it.name } ?: "None"
        }
        post(EditorInitializedEvent())
    }

    private fun createPlugin(plugin: Plugin) {
        profile("load plugin ${plugin.name}") {
            plugin.init(engine as Engine)

            plugin.systems.forEach { system ->
                index.list(System::class.java)?.add(system)
            }
            plugin.tools.forEach { tool ->
                index.list(Tool::class.java)?.add(tool)
                if (tool.activationKey != -1) {
                    registerHotkey(tool.activationKey) {
                        tool.toggle()
                    }
                }
            }

            plugin.registry(InjectionContext)

            index.list(Plugin::class.java)?.add(plugin)

        }

    }



    /**
     * Configures the initial pipeline state; further routines are later injected as needed
     * This is everything required to render the full editor application & scene description (graph, environment, etc)
     *
     * we can extend this to include thing like custom shaders, more intricate pipelines like deferred rendering, shadow maps,
     * etc by either creating new routines nested inside what we have here, or by creating new states as we feel we need them in code
     *
     * for instance, a GBuffer implementation would require subividing the depth and color passes further to capture more information
     * about the scene, and then a final pass to composite the final image
     *
     * @see State
     * @see push
     */
    private fun buildPipeline() {

        val color = buildBuffer("color",true)
        val depth =  buildBuffer("depth",true)

        buffers["depth"] = depth
        buffers["color"] = color

//        onWindowResized { event ->
            once(INIT) {
                buffers["depth"] = FBO.ensureScreenSize(buffers["depth"]!!, GLFormat.RGBA32, true)
                buffers["color"] = FBO.ensureScreenSize(buffers["color"]!!, GLFormat.RGBA32, true)
            }


//        }

            scene.apply {
                context.run {
                    push(INIT) { delta ->
                        clearScreen(.1f,.1f,.1f,0f)

                    }
                    push(UI_LOGIC,transition = {  }) { delta ->
                        ui.act()
                        gui.viewportWidget.update()

                    }
                    push(PREPARE_SCENE) { delta ->
                        scene.update(delta)
                        viewportController.update()
                        scene.renderer.prepareContext(scene.context,true)
                    }
                    push(BEFORE_DEPTH_PASS) { delta ->
                        scene.renderer.renderContext(scene.context)
                    }
                    push(BEFORE_COLOR_PASS) { _ ->
                        scene.renderer.prepareContext(scene.context, false)
                    }
                    push(COLOR_PASS) { _ ->
                            gui.updateviewport()
                            modelBatch.begin(perspectiveCamera)
                            graph.root.renderColor(delta, modelBatch, scene.context)
                            graph.root.renderDebug(scene.context)
                            modelBatch.end()

                    }

                    push(BEFORE_UI_PASS) {_ ->
                        with(Outliner.settings) {
                            outliner.render(spriteBatch,depthTex,perspectiveCamera)
                        }
                    }

                    push(UI_PASS) { delta ->
                        ui.viewport.apply()
                        ui.draw()

                        UI.DialogStage.apply {
                            act()
                            draw()
                        }
                    }
                }
            }
    }

    /**
     * Render pipeline first, then process everything else. Everything after the pipeline is auxillary
     */
    override fun render() {
        val delta = Gdx.graphics.deltaTime
        clearColor()
        clearDepth()

        scene.apply {context.run {
            this@Salient.render(delta)
        } }
        update(delta)
    }

    context(SceneContext,Scene) fun render(delta:Float) {


    }

    override fun resize(width: Int, height: Int) {
        UI.resize(width, height)
//        post(WindowResizedEvent(width, height),false)
        super.resize(width, height)
    }

    override fun dispose() {
        super.dispose()
        UI.dispose()
    }

    private fun configureUI() {
        gui = Gui()
        gui.configure()
        singleton(gui)
        ui.layout(gui)
        gui.restore(Settings.i.ui)
        post(InterfaceInitializedEvent())
    }

    private fun configureInput() {
        viewportController = ViewportController()
        singleton(viewportController)
        viewportController.actor = gui.viewportWidget
        gui.viewportWidget.addListener(viewportController)
        gui.viewportWidget.addListener(viewportController.clickListener)
        val historyListener = ActionHistoryKeyListener(inject())
        Input.addProcessor(historyListener)
        Input.addProcessor(UI)
    }

    companion object : Pipeline()

}

fun salient(run: Salient.() -> Unit) {
    val salient : Salient = inject()
    salient.run()
}


val index : Index<Named> by lazy { inject() }

fun <T: Plugin> plugin(type: KClass<T>, use:T.()->Unit)  {
    val plugin =  index.list(Plugin::class.java)?.find { it::class == type } as T?
    plugin?.let { use(it) }
}

fun <T: Tool> tool(type: KClass<T>, use:T.()->Unit)  {
    val tool =  index.list(Tool::class.java)?.find { it::class == type } as T?
    tool?.let { use(it) }
}






