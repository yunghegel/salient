package org.yunghegel.salient.editor.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.project.ProjectManager
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.asset.props
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.graphics.shapes.Primitive
import org.yunghegel.salient.engine.graphics.shapes.PrimitiveModel
import org.yunghegel.salient.engine.graphics.shapes.ShapeParameters
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.component.MaterialsComponent
import org.yunghegel.salient.engine.scene3d.component.ModelComponent
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.widgets.value.ReflectionBasedEditor.AccessorRegistry.name

object ObjectFactory {
//
//    val state: State
//        get() = inject()

    val assetManager : AssetManager
        get() = inject()

    val projectManager : ProjectManager
        get() = inject()

    val sceneManager : SceneManager
        get() = inject()

//
//    fun importModel(path: FileHandle) {
//        val (project, scene) = state
//        val handle : AssetHandle = assetManager.createHandle(path)
//        assetManager.indexHandle(handle,project)
//        val asset = assetManager.includeAsset(handle,scene)
//        val go = newGameObject(path.nameWithoutExtension(),scene)
//        val modelComponent = ModelComponent(handle,go)
//        go.add(modelComponent)
//
//        val mats = go[MaterialsComponent::class]?.value
//    }

    fun createPrimitive(primitive: Primitive,scene: Scene) {
        val go = newGameObject(primitive.name,scene)
        val path = ShapeParameters.generatePath(projectManager.currentProject!!, primitive.defaultParams)
        val uuid : String = "primitive_${primitive.name}${primitive.defaultParams.hashCode()}"
        val handle = assetManager.createHandle(Gdx.files.absolute(path),uuid,props("type" to "primitive", "kind" to primitive.name, "params" to primitive.defaultParams))
        assetManager.indexHandle(handle,projectManager.currentProject!!)
        val asset = assetManager.includeAsset(handle,scene)
        val modelComponent = ModelComponent(handle,go)
        go.add(modelComponent)
    }

    fun createPrimitive(primitive: Primitive,scene: Scene, params: ShapeParameters) : AssetHandle {
        val go = newGameObject(primitive.name,scene)
        val path = ShapeParameters.generatePath(projectManager.currentProject!!, params)
        val uuid : String = "primitive${primitive.name}_${params.hashCode()}"
        val handle = assetManager.createHandle(Gdx.files.absolute(path),uuid,props("type" to "primitive", "kind" to primitive.name, "params" to params))
        assetManager.indexHandle(handle,projectManager.currentProject!!)
        assetManager.serializeHandle(handle,projectManager.currentProject!!,scene)
        val asset = assetManager.includeAsset(handle,scene)
        val modelComponent = ModelComponent(handle,go)
        go.add(modelComponent)
        return handle
    }

    fun writeAsset(asset: Asset<*>) {

    }

    fun newGameObject(name: String,scene:Scene, parent: GameObject? = null) : GameObject {
        val go = GameObject(name,scene = scene)
        scene.graph.addGameObject(go,parent)
        return go
    }




}