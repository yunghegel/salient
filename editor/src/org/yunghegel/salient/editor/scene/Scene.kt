package org.yunghegel.salient.editor.scene

import com.badlogic.gdx.files.FileHandle
import kotlinx.serialization.Serializable
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.project.ProjectManager
import org.yunghegel.salient.engine.api.asset.type.spec.MaterialSpec
import org.yunghegel.salient.engine.api.properties.NamedObjectResource
import org.yunghegel.salient.engine.api.dto.DTOAdapter
import org.yunghegel.salient.engine.api.dto.SceneDTO
import org.yunghegel.salient.engine.api.dto.datatypes.CameraData
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.api.scene.SceneEnvironment
import org.yunghegel.salient.engine.events.lifecycle.onShutdown
import org.yunghegel.salient.engine.helpers.Ignore
import org.yunghegel.salient.engine.helpers.SFile
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.SceneContext
import org.yunghegel.salient.engine.scene3d.SceneRenderer
import org.yunghegel.salient.engine.scene3d.component.MaterialsComponent
import org.yunghegel.salient.engine.scene3d.component.RenderableComponent
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.system.provide
import org.yunghegel.salient.engine.system.singleton

typealias SceneFile = SFile
typealias SceneDirectory = SFile



class Scene(val handle:SceneHandle, val project: Project) : EditorScene(handle), NamedObjectResource by handle {


    val manager: SceneManager by lazy { inject() }

    @Ignore
    override val graph = SceneGraph(this)

    @Ignore
    override val context: SceneContext = SceneContext(this)

    @Ignore
    override val renderer: SceneRenderer<Scene, SceneGraph>  = SceneRenderer(this)


    @Ignore
    override val selection = GameObjectSelectionManager(graph.selection)

    init {
        singleton(selection)
        context.set(this)

        onShutdown {
            manager.saveScene(this)
        }
        provide<SceneContext> { context }
        provide<SceneRenderer<Scene, SceneGraph>> { renderer }
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

        override fun fromDTO(dto: SceneDTO) : Scene {
            val scene = Scene(dto.handle!!, inject())
            val assets : AssetManager = inject()
            val project : ProjectManager = inject()
            assets.initializeScene(scene,project.currentProject!!)
//            dto.assetIndex.forEach { asset ->
//                scene.indexAsset(asset)
//            }
            CameraData.applyDTO(scene.context.perspectiveCamera, dto.scene_context.cameraSettings)
            SceneEnvironment.applyDTO(scene.context, dto.scene_context.sceneEnvironment)
            SceneGraph.applyDTO(dto.scene_graph,scene.graph)
            return scene
        }

        override fun toDTO(model: Scene): SceneDTO {
            val dto =  SceneDTO()
            model.retrieveAssetIndex().forEach {
                dto.asset_index.add(it)
            }
            dto.handle = model.handle
            dto.scene_context.sceneEnvironment = SceneEnvironment.toDTO(model.context)
            dto.scene_context.cameraSettings = CameraData.toDTO(model.context.perspectiveCamera)
            dto.scene_graph = SceneGraph.toDTO(model.graph)
            model.graph.traverse { go ->
                go.components.forEach { cmp ->
                    if (cmp is MaterialsComponent) {
                        cmp.materials.forEach { material ->
                            val spec = MaterialSpec.fromMaterial(material)
                            dto.scene_materials.materials.add(spec)
                        }
                    }
                }
            }

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