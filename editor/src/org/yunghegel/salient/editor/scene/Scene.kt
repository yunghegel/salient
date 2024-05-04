package org.yunghegel.salient.editor.scene

import com.badlogic.gdx.files.FileHandle
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.input.EditorCamera
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.engine.api.NamedObjectResource
import org.yunghegel.salient.engine.api.dto.DTOAdapter
import org.yunghegel.salient.engine.api.dto.SceneDTO
import org.yunghegel.salient.engine.api.dto.datatypes.CameraData
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.api.scene.SceneEnvironment
import org.yunghegel.salient.engine.events.lifecycle.onShutdown
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.SceneContext
import org.yunghegel.salient.engine.scene3d.SceneRenderer
import org.yunghegel.salient.engine.scene3d.component.RenderableComponent
import org.yunghegel.salient.engine.system.file.Paths
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.system.singleton

typealias SceneFile = FileHandle
typealias SceneDirectory = FileHandle

class Scene(val handle:SceneHandle, val project: Project, val manager: SceneManager) : EditorScene(handle,manager), NamedObjectResource by handle {

    override val graph = SceneGraph(this)

    override val context: SceneContext

    override val renderer: SceneRenderer<Scene, SceneGraph>

    val selection = GameObjectSelectionManager(graph.selection)

    init {
        singleton(selection)
        context = SceneContext(this)
        context.set(this)
        renderer = SceneRenderer(this)
        onShutdown {
            manager.saveScene(this)
        }
    }

    override fun update(delta: Float) {
        renderer.updateGraph(this, graph.root, context)
    }

    override fun render(delta: Float) {
        renderer.renderContext(context)
        renderer.renderGraph(this, graph, context)
    }

    fun render(go: GameObject, parent: GameObject? = null) {
        go.components.forEach { cmp ->
            if (cmp is RenderableComponent) {
                cmp.render(context.modelBatch,context.perspectiveCamera,context)
            }
        }
        go.getChildren().forEach { render(it, go) }
    }

    companion object Data : DTOAdapter<Scene, SceneDTO> {

        override fun fromDTO(dto: SceneDTO) : Scene{
            val scene = Scene(dto.handle!!, inject(), inject())
            val assets : AssetManager = inject()
            dto.assetIndex.forEach { asset ->
                assets.includeAsset(asset,scene)
            }
            CameraData.applyDTO(scene.context.perspectiveCamera, dto.sceneContext.cameraSettings)
            SceneEnvironment.applyDTO(scene.context, dto.sceneContext.sceneEnvironment)
            SceneGraph.applyDTO(dto.sceneGraph,scene.graph)
            return scene
        }

        override fun toDTO(model: Scene): SceneDTO {
            val dto =  SceneDTO()
            model.retrieveAssetIndex().forEach {
                dto.assetIndex.add(it)
            }
            dto.handle = model.handle
            dto.sceneContext.sceneEnvironment = SceneEnvironment.toDTO(model.context)
            dto.sceneContext.cameraSettings = CameraData.toDTO(model.context.perspectiveCamera)
            dto.sceneGraph = SceneGraph.toDTO(model.graph)

            return dto
        }
    }

    override fun resize(width: Int, height: Int) {
        context.resize(width, height)
    }

    override fun dispose() {
        context.dispose()
    }

}