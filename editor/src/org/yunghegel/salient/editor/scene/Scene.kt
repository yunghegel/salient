package org.yunghegel.salient.editor.scene

import com.badlogic.gdx.files.FileHandle
import kotlinx.serialization.Transient
import org.yunghegel.salient.editor.app.dto.DTOAdapter
import org.yunghegel.salient.editor.app.dto.SceneDTO
import org.yunghegel.salient.editor.app.dto.SceneEnvironmentDTO
import org.yunghegel.salient.editor.app.dto.datatypes.CameraData
import org.yunghegel.salient.editor.input.EditorCamera
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.engine.api.NamedObjectResource
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.graphics.scene3d.GameObject
import org.yunghegel.salient.engine.graphics.scene3d.SceneContext
import org.yunghegel.salient.engine.graphics.scene3d.SceneRenderer
import org.yunghegel.salient.engine.graphics.scene3d.component.RenderableComponent
import org.yunghegel.salient.engine.io.Paths
import org.yunghegel.salient.engine.io.inject

typealias SceneFile = FileHandle
typealias SceneDirectory = FileHandle

class Scene(val handle:SceneHandle, val project: Project, val manager: SceneManager) : EditorScene(handle,manager), NamedObjectResource by handle {

    val folder : SceneDirectory = Paths.SCENE_DIR_FOR(project.name).handle

    val file : SceneFile  = Paths.SCENE_FILE_FOR(project.name,handle.name).handle

    override val sceneGraph = SceneGraph(this)

    @Transient
    override val context: SceneContext
    @Transient
    override val renderer: SceneRenderer<Scene, SceneGraph>

    @Transient
    val editorCamera : EditorCamera

    init {
        context = SceneContext(this)
        context.set(this)
        renderer = SceneRenderer(this)
        editorCamera = EditorCamera(inject(), inject())

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
            dto.assetUsage.forEach { asset ->
                scene.assetUsage.add(asset)
            }
            CameraData.applyToCamera(scene.context.perspectiveCamera, dto.sceneContext.cameraSettings)
            SceneEnvironmentDTO.applyToEnvironment(scene.context, dto.sceneContext.sceneEnvironment)
            return scene
        }

        override fun toDTO(model: Scene): SceneDTO {
            val dto =  SceneDTO()
            dto.handle = model.handle
            dto.assetUsage = Array(model.assetUsage.size) {model.assetUsage[it]}
            dto.sceneContext.sceneEnvironment = dto.sceneContext.sceneEnvironment.toDTO(model.context)
            dto.sceneContext.cameraSettings = CameraData.toDTO(model.context.perspectiveCamera)

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