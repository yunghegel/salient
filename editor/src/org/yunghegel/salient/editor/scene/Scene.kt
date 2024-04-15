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
import org.yunghegel.salient.engine.graphics.scene3d.GameObject
import org.yunghegel.salient.engine.graphics.scene3d.SceneContext
import org.yunghegel.salient.engine.graphics.scene3d.SceneRenderer
import org.yunghegel.salient.engine.graphics.scene3d.component.RenderableComponent
import org.yunghegel.salient.engine.system.file.Paths
import org.yunghegel.salient.engine.system.inject

typealias SceneFile = FileHandle
typealias SceneDirectory = FileHandle

class Scene(val handle:SceneHandle, val project: Project, val manager: SceneManager) : EditorScene(handle,manager), NamedObjectResource by handle {

    val folder : SceneDirectory = Paths.SCENE_DIR_FOR(project.name).handle

    val file : SceneFile  = Paths.SCENE_FILE_FOR(project.name,handle.name).handle

    override val sceneGraph = SceneGraph(this)

    override val context: SceneContext

    override val renderer: SceneRenderer<Scene, SceneGraph>

    val editorCamera : EditorCamera

    init {
        context = SceneContext(this)
        context.set(this)
        renderer = SceneRenderer(this)
        editorCamera = EditorCamera(inject(), inject())
        onShutdown {
            manager.saveScene(this)
        }

    }

    override fun update(delta: Float) {
        renderer.updateGraph(this, sceneGraph.root, context)
    }

    override fun render(delta: Float) {
        renderer.renderContext(context)
        renderer.renderGraph(this, sceneGraph, context)
    }

    fun render(go: GameObject, parent: GameObject? = null) {
        go.components.forEach { cmp ->
            if (cmp is RenderableComponent) {
                context.modelBatch.render(cmp, context)
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
            SceneGraph.applyDTO(dto.sceneGraph,scene.sceneGraph)
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
            dto.sceneGraph = SceneGraph.toDTO(model.sceneGraph)

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