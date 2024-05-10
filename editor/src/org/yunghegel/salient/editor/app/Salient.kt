package org.yunghegel.salient.editor.app

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.utils.ScreenUtils
import com.github.czyzby.kiwi.util.gdx.viewport.Viewports.update
import ktx.app.clearScreen
import ktx.async.KtxAsync
import org.yunghegel.gdx.utils.data.Index
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.core.graphics.util.OutlineDepth
import org.yunghegel.salient.editor.app.configs.Settings
import org.yunghegel.salient.editor.input.ViewportController
import org.yunghegel.salient.editor.plugins.gizmos.GizmoPlugin
import org.yunghegel.salient.editor.plugins.outline.OutlinerPlugin
import org.yunghegel.salient.editor.plugins.outline.lib.Outliner
import org.yunghegel.salient.editor.plugins.picking.PickingPlugin
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.scene.SceneGraph
import org.yunghegel.salient.engine.InterfaceInitializedEvent
import org.yunghegel.salient.engine.Pipeline
import org.yunghegel.salient.engine.State.*
import org.yunghegel.salient.engine.api.Resizable
import org.yunghegel.salient.engine.api.ecs.DEBUG_ALL
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.undo.ActionHistoryKeyListener
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.lifecycle.EditorInitializedEvent
import org.yunghegel.salient.engine.events.lifecycle.WindowResizedEvent
import org.yunghegel.salient.engine.graphics.GFX
import org.yunghegel.salient.engine.scene3d.SceneContext
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.plugin.Plugin
import org.yunghegel.salient.engine.scene3d.component.BoundsComponent
import org.yunghegel.salient.engine.scene3d.component.MaterialsComponent
import org.yunghegel.salient.engine.scene3d.component.MeshComponent
import org.yunghegel.salient.engine.scene3d.component.ModelComponent
import org.yunghegel.salient.engine.scene3d.component.RenderableComponent
import org.yunghegel.salient.engine.system.*
import org.yunghegel.salient.engine.tool.Tool
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.layout.EditorFrame


/**
 * Application entry point. Responsible for building all resource and rendering pipelines and bootstrapping the editor context
 */

class Salient : ApplicationAdapter() {


    val app = App()

    var gui: Gui by notnull()

    var scene: Scene by notnull()

    var project: Project by notnull()

    private var state: State by notnull()

    val pipeline: Pipeline = Companion


    val index = Index<Named>()


    init {
        index.types[Tool::class.java] = mutableListOf()
        index.types[System::class.java] = mutableListOf()
        index.types[Plugin::class.java] = mutableListOf()
    }

    override fun create() {

    /* allow asyncronous actions when we need it */
    threading.init()
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

    val (picking, gizmo, outline) = listOf(PickingPlugin(), GizmoPlugin(), OutlinerPlugin()).onEach { plugin ->
        createPlugin(plugin)
    }

    scene.manager.saveScene(scene)
    post(EditorInitializedEvent())

    }

    fun createPlugin(plugin: Plugin) {
        profile("load plugin ${plugin.name}") {
            plugin.init(engine as Engine)
            plugin.systems.forEach { index.list(System::class.java)?.add(it) }
            plugin.tools.forEach { index.list(Tool::class.java)?.add(it) }
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

        val color = buildBuffer("color")
        val depth =  buildBuffer("depth",false)

        buffers["depth"] = depth
        buffers["color"] = color

            push(INIT) { delta ->
                clearScreen(.1f,.1f,.1f,0f)
            }
            push(UI_LOGIC,transition = {  }) { delta ->
                ui.act()
                gui.viewportWidget.update()
            }
            push(PREPARE_SCENE) { delta ->
                scene.update(delta)
                scene.renderer.prepareContext(scene.context,true)
            }
            push(BEFORE_DEPTH_PASS) { delta ->
                scene.renderer.renderContext(scene.context)
            }
            push(BEFORE_COLOR_PASS) { _ ->
                scene.renderer.prepareContext(scene.context,false)
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

//        once(DEPTH_PASS) { _ ->
//            buffers["color"] = color
//            depthTex = pass(depth) {
//            clearScreen(0f, 0f, 0f, 0f)
//            depthBatch.begin(context.perspectiveCamera)
//            graph.root.renderDepth(delta, depthBatch, context)
//            depthBatch.end()
//            depthTex = depth.colorBufferTexture
//            }
//        }
        once(COLOR_PASS) { _ ->
            colorTex = pass(buffers["color"]!!) {
                clearScreen(0f, 0f, 0f, 0f)
                gui.updateviewport()
                modelBatch.begin(perspectiveCamera)
                graph.root.renderColor(delta, modelBatch, scene.context)
                graph.root.renderDebug(scene.context)
                modelBatch.end()
            }
            colorTex?.draw(spriteBatch)
        }
        val outliner : OutlineDepth = inject()

        once(OVERLAY_PASS) {_ ->
            val buf = buffers["outline"]
            with(Outliner.settings) {
                outliner.render(spriteBatch,depthTex,perspectiveCamera)
            }
        }
    }

    override fun resize(width: Int, height: Int) {
        UI.resize(width, height)
        post(WindowResizedEvent(width, height),false)
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
        val viewportController = ViewportController()
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

fun sampleSceneGraph(graph:SceneGraph) {

    val model = ObjLoader().loadModel(Gdx.files.internal("models/obj/TorusKnot.obj"))
    val mats = model.materials
    val mesh = model.meshes
    val bounds = BoundsComponent.getBounds(model)

    val go =graph.newFromRoot("TorusKnot").apply {
        val modelComponent = ModelComponent(AssetHandle(Gdx.files.internal("models/obj/TorusKnot.obj")),this)
        val materialsCompenent = MaterialsComponent(mats,this)
        val meshComponent = MeshComponent(mesh,this)
        val boundsComponent = BoundsComponent(bounds,this)


        add(modelComponent)
        add(materialsCompenent)
        add(meshComponent)
        add(boundsComponent)

        val renderable = RenderableComponent(ModelInstance(model),this)
        add(renderable)
    }

    go.setFlag(DEBUG_ALL)
    go.tag("model")
    graph.addGameObject(go, graph.root)

}




