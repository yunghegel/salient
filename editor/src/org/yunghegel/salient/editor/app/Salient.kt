package org.yunghegel.salient.editor.app

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.utils.ScreenUtils
import ktx.async.KtxAsync
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.editor.app.configs.Settings
import org.yunghegel.salient.editor.input.ViewportController
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.render.systems.SelectionSystem
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.scene.SceneGraph
import org.yunghegel.salient.engine.Pipeline
import org.yunghegel.salient.engine.State.*
import org.yunghegel.salient.engine.api.Resizable
import org.yunghegel.salient.engine.api.ecs.DEBUG_ALL
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.undo.ActionHistoryKeyListener
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.lifecycle.EditorInitializedEvent
import org.yunghegel.salient.engine.events.lifecycle.WindowResizedEvent
import org.yunghegel.salient.engine.graphics.GFX
import org.yunghegel.salient.engine.graphics.scene3d.component.*
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.system.singleton
import org.yunghegel.salient.engine.ui.UI


/**
 * Application entry point. Responsible for building all resource and rendering pipelines and bootstrapping the editor context
 */

class Salient : ApplicationAdapter() {


    val app = App()

    var gui : Gui by notnull()

    var scene : Scene by notnull()

    var project : Project by notnull()

    private var state : State by notnull()



    val resizeListeners = mutableListOf<Resizable>()
    init {
    }


    override fun create() {
        KtxAsync.initiate()

        singleton(this)
        run {
            singleton(app)
            singleton<Engine>(engine as Engine)
            singleton<Pipeline>(engine as Pipeline)
        }

        listOf(app,UI,GFX,Input).forEach { module ->
            module.initialize()
        }


        UI.buildSharedContext()
        GFX.buildSharedContext()
        state = app.bootstrap() ?: throw IllegalStateException("Scene not found")
        project = state.first
        scene = state.second


        addSystem(SelectionSystem())

        initSystems()
        buildPipeline()

        configureUI()
        configureInput()

        scene.manager.saveScene(scene)
        post(EditorInitializedEvent())



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

            push(INIT) { delta ->
                clearColor()
                clearDepth()
                ScreenUtils.clear(0.12f,0.12f,0.12f,1f)
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
            push(DEPTH_PASS) { _ ->
                scene.renderer.renderDepth(scene,scene.context)
                gui.viewportWidget.viewportWidget.updateViewport(false)
            }
            push(BEFORE_COLOR_PASS) { _ ->
                scene.renderer.prepareContext(scene.context,false)
            }
            push(COLOR_PASS) { delta ->
                scene.renderer.renderColor(scene,scene.context)
                scene.renderer.renderDebug(scene,scene.context)
            }
            push(UI_PASS) { delta ->
                ui.viewport.apply()
                ui.draw()
            }

    }


    /**
     * Render pipeline first, then process everything else. Everything after the pipeline is auxillary
     */
    override fun render() {
        val delta = Gdx.graphics.deltaTime
        update(delta)

    }



    private var first = true

    override fun resize(width: Int, height: Int) {
        UI.resize(width, height)
        resizeListeners.forEach { it.resize(width, height) }
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

        resizeListeners.add(ui)


        with(Settings.i.ui) {

            gui.apply {
                split.cache.put(0, leftLayout.splitAmount)
                split.cache.put(1, rightLayout.splitAmount)
                split.setSplit(0, leftLayout.splitAmount)
                split.setSplit(1, rightLayout.splitAmount)
                gui.centerSplit.setSplitAmount(bottomLayout.splitAmount)
            }
            if (leftLayout.hidden) {
                gui.split.hide(0, LEFT)
            } else {

            }
            if (rightLayout.hidden) {
                gui.split.hide(1, RIGHT)
            } else {

            }
            if (bottomLayout.hidden) {
                gui.centerSplit.hide(BOTTOM)
            } else {

            }
        }
    }

    private fun configureInput() {
        val viewportController = ViewportController()
        viewportController.actor = gui.viewportWidget
        gui.viewportWidget.addListener(viewportController)
        gui.viewportWidget.addListener(viewportController.clickListener)
        val historyListener = ActionHistoryKeyListener(inject())
        Input.addProcessor(historyListener)
        Input.addProcessor(UI)
        UI.root.configureListener(gui.viewportWidget,viewportController)
//        Input.addProcessor(scene.editorCamera.perspectiveCameraController)
    }

    companion object : Pipeline()

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

fun addResizer(resizable: Resizable) {
    val salient : Salient = inject()
    salient.resizeListeners.add(resizable)
}


