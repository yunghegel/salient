package org.yunghegel.salient.editor.app

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Screen
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.gdx.utils.ext.clearColor
import org.yunghegel.gdx.utils.ext.clearDepth
import org.yunghegel.gdx.utils.ext.notnull
import org.yunghegel.salient.core.graphics.util.OutlineDepth
import org.yunghegel.salient.editor.app.configs.Settings
import org.yunghegel.salient.editor.input.ViewportController
import org.yunghegel.salient.editor.modules.ApplicationModle
import org.yunghegel.salient.editor.plugins.base.DefaultPlugin
import org.yunghegel.salient.editor.plugins.base.systems.registerHotkey
import org.yunghegel.salient.editor.plugins.gizmos.GizmoPlugin
import org.yunghegel.salient.editor.plugins.intersect.IntersectionPlugin
import org.yunghegel.salient.editor.plugins.outline.OutlinerPlugin
import org.yunghegel.salient.editor.plugins.picking.PickingPlugin
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.input.InterfaceInitializedEvent
import org.yunghegel.salient.editor.modules.GFXModule
import org.yunghegel.salient.engine.Startup
import org.yunghegel.salient.editor.plugins.rendering.State.*
import org.yunghegel.salient.editor.modules.ui
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.api.plugin.Plugin
import org.yunghegel.salient.engine.api.tool.InputTool
import org.yunghegel.salient.engine.api.tool.Tool
import org.yunghegel.salient.engine.api.undo.ActionHistoryKeyListener
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.lifecycle.EditorInitializedEvent
import org.yunghegel.salient.editor.modules.InputModule
import org.yunghegel.salient.editor.modules.buffers
import org.yunghegel.salient.engine.GraphicsModule
import org.yunghegel.salient.engine.Module
import org.yunghegel.salient.engine.api.undo.ActionHistory
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.events.lifecycle.ModuleCreatedEvent
import org.yunghegel.salient.engine.graphics.GFX
import org.yunghegel.salient.engine.system.*
import org.yunghegel.salient.engine.ui.UI
import kotlin.collections.set
import kotlin.reflect.KClass


/**
 * Application entry point. Responsible for building all resource and rendering pipelines and bootstrapping the editor context
 */

@Suppress("UNCHECKED_CAST")
class Salient : Engine(), Screen {


    lateinit var app : ApplicationModle
    lateinit var gfxModule: GFXModule
    lateinit var input : InputModule

    var gui: Gui by notnull()

    var scene: Scene by notnull()

    var project: Project by notnull()

    val registry = Registry

    val index = Index<Named>()


    lateinit var viewportController: ViewportController

    init {

    }

    val outliner: OutlineDepth by lazy { inject() }


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
     */
    private fun buildPipeline() {

        val color = gfxModule.buildBuffer("color", true)
        val depth = gfxModule.buildBuffer("depth", true)


        buffers["depth"] = depth
        buffers["color"] = color


//        onEditorInitialized {
//            scene.apply {
//                context.run {
//                    gfxModule.push(INIT) { delta ->
//                        clearScreen(.1f, .1f, .1f, 0f)
//                    }
//                    gfxModule.push(UI_LOGIC, transition = { }) { delta ->
//                        ui.act(delta)
//                        gui.viewportWidget.update()
//                    }
//                    gfxModule.push(PREPARE_SCENE) { delta ->
//                        scene.update(delta)
//                        viewportController.update()
//                        scene.renderer.prepareContext(scene.context, true)
//                    }
//                    gfxModule.push(BEFORE_DEPTH_PASS) { delta ->
//                        scene.renderer.renderContext(scene.context)
//                    }
//                    gfxModule.push(BEFORE_COLOR_PASS) { _ ->
//                        scene.renderer.prepareContext(scene.context, false)
//                    }
//                    gfxModule.push(COLOR_PASS) { _ ->
//                        gui.updateviewport()
//                        modelBatch.begin(perspectiveCamera)
//                        graph.root.renderColor(delta, modelBatch, scene.context)
//                        graph.root.renderDebug(scene.context)
//                        modelBatch.end()
//
//                        with(gui.viewportWidget.compass) {
//                            val pos = toOpenGLCoords(gui.viewportWidget.getBounds().topRight()).sub(0.06f, 0.065f)
//                            setPos(0.94f, 0.935f)
//                            update(Gdx.graphics.deltaTime, gui.viewportWidget.width, gui.viewportWidget.height)
//                            render(delta)
//                        }
//                    }
//
//                    gfxModule.push(BEFORE_UI_PASS) { _ ->
//                        with(Outliner.settings) {
//                            outliner.render(spriteBatch, gfxModule.depthTex, perspectiveCamera)
//                        }
//                    }
//
//                    gfxModule.push(UI_PASS) { delta ->
//                        ui.viewport.apply()
//                        ui.draw()
//                        UI.DialogStage.apply {
//                            act()
//                            draw()
//                        }
//                    }
//                }
//            }
//        }


    }


    override fun show() {
        async.init()
        Startup.start()
        run {
            singleton(this)
            singleton(index)
            singleton<Engine>(this as Engine)
        }

        run {
            GFX.bindvalues(InjectionContext)
        }
        UI.init()

        app = ApplicationModle()
        gfxModule = GFXModule()
        input = InputModule()
        loadModule(app)

        val state = app.bootstrap()
        val (project, scene) = state

        this.project = project
        this.scene = scene

        configureUI()

        listOf(
            gfxModule,
            input
        ).sortedBy { it.priority }
            .forEach { module ->
                debug("loading module ${module::class.simpleName} with priority ${module.priority}")
                loadModule(module)
            }

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

        post(EditorInitializedEvent())
    }


   fun loadModule(module: Module) {
        profile("load module") {
            debug("loading module ${module::class.simpleName} with priority ${module.priority}")
            module.beforeEval.forEach { it() }
            module.plugins.forEach { pluginClass ->
                debug("loading plugin ${pluginClass.simpleName} for module ${module::class.simpleName}")

//                find plugin instance
                val plugin = pluginClass.constructors.first().call()


                createPlugin(plugin)
            }
            register {
                module.registry(this)
            }
            module.afterEval.forEach { it() }
            Bus.post(ModuleCreatedEvent(module) )

        }
    }


    private fun createPlugin(plugin: Plugin) {
        profile("load plugin ${plugin.name}") {
            plugin.init(this)

            plugin.systems.forEach { system ->
                debug("loading plugin ${system::class.simpleName} with priority ${system.priority}")
                index.list(System::class.java)?.add(system)
                addSystem(system)
            }
            plugin.tools.forEach { tool ->
                debug("loading tool ${tool::class.simpleName} with activation key ${tool.activationKey}")
                index.list(Tool::class.java)?.add(tool                                                                                                                          )
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

    override fun render(delta: Float) {
        clearColor()
        clearDepth()
        update(delta)
    }


    override fun resize(width: Int, height: Int) {
        UI.resize(width, height)
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {

    }

    override fun dispose() {
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
        input.addProcessor(historyListener)
        input.addProcessor(UI)
    }


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

fun <T : InputTool> tool(type: KClass<T>, use: T.() -> Unit) {
    val tool = index.list(InputTool::class.java)?.find { it::class == type } as T?
    tool?.let { use(it) }
}






