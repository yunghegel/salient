package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import org.yunghegel.gdx.utils.data.serialize
import org.yunghegel.salient.engine.api.asset.EditorAssetManager
import org.yunghegel.salient.engine.api.asset.locateAsset
import org.yunghegel.salient.engine.api.asset.type.ModelAsset
import org.yunghegel.salient.engine.api.dto.component.ModelComponentDTO
import org.yunghegel.salient.engine.api.ecs.AssetUsage
import org.yunghegel.salient.engine.api.ecs.BaseComponent
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.graphics.shapes.Primitive
import org.yunghegel.salient.engine.graphics.shapes.PrimitiveModel
import org.yunghegel.salient.engine.graphics.shapes.ShapeParameters
import org.yunghegel.salient.engine.helpers.Serializer.yaml
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.ModelRenderable
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.system.inject

import kotlin.reflect.KClass


class ModelComponent(val handle: AssetHandle, go: GameObject) : EntityComponent<Model>(null,go), AssetUsage<Model,ModelAsset> {

    override val iconName: String = "model_object"
    override val type: KClass<out BaseComponent> = ModelComponent::class

    var asset : ModelAsset? = null

    val exportState : MutableMap<String,String> = mutableMapOf()

    init {
        handle.locateAsset(go.scene)?.let { asset ->
            require(asset is ModelAsset) { "Asset is not a ModelAsset" }
            this.asset = asset
            if (!asset.loaded) asset.load()
            value = asset.value!!
            applyAsset(value!!)
        }
    }

    enum class Type {
        FILE,
        PRIMITIVE
    }

    companion object {
        fun toDTO(model: ModelComponent, go: GameObject) : ModelComponentDTO {
            return ModelComponentDTO().apply {
                asset = model.handle.uuid
                importParams = model.exportState(model.asset!!)
                strategy = if (model.asset?.path == Filepath.GENERATIVE) Type.PRIMITIVE else Type.FILE
            }
        }

        fun fromDTO(dto: ModelComponentDTO,scene: EditorScene,gameObject: GameObject) : ModelComponent? {
            if (dto.strategy== Type.FILE) {
                scene.index.find { it.uuid == dto.asset}?.let { handle ->
                    info("Found asset handle ${handle.uuid}")
                    return ModelComponent(handle,gameObject)
                }
            }
            return null
        }
    }

    override fun locateAsset(assetHandle: AssetHandle): ModelAsset? {
        assetHandle.locateAsset(go.scene)?.let { asset ->
            require(asset is ModelAsset) { "Asset is not a ModelAsset" }
            return asset
        } ?: return null
    }

    override fun importState(state: Map<String, String>): ModelAsset {
        when(state["type"]) {
            "file" -> {
                val id = state["handle"] ?: throw IllegalArgumentException("No handle")
                val asset = go.scene.retrieveAssetIndex().find { it.uuid == id }?.let { handle ->
                    handle.locateAsset(go.scene)?.let { asset ->
                        require(asset is ModelAsset) { "Asset is not a ModelAsset" }
                        asset
                    }
                }
                return asset ?: throw IllegalStateException("Asset not found")
            }
            "primitive" -> {
                val kind : Primitive = Primitive.valueOf(state["kind"] ?: throw IllegalArgumentException("No kind"))
                val params : ShapeParameters = yaml.decodeFromString(ShapeParameters.serializer(),state["params"] ?: throw IllegalArgumentException("No params"))
                val primModel : PrimitiveModel = Primitive.fromParams(params)
                val id = state["handle"] ?: throw IllegalArgumentException("No handle")
                val asset = go.scene.retrieveAssetIndex().find { it.uuid == id }?.let { handle ->
                    handle.locateAsset(go.scene)?.let { asset ->
                        require(asset is ModelAsset) { "Asset is not a ModelAsset" }
                        asset
                    }
                }
                return asset ?: throw IllegalStateException("Asset not found")

            }
            else -> throw IllegalArgumentException("Invalid type")
        }
    }

    override fun exportState(asset: ModelAsset): MutableMap<String, String> {
        return mutableMapOf(
            "handle" to asset.handle.uuid,
            "params" to handle.extras.serialize()
        )
    }

    override fun applyAsset(asset: Model) {
        go.remove(RenderableComponent::class.java)
        go.remove(PickableComponent::class.java)
        go.remove(MaterialsComponent::class.java)
        go.remove(MeshComponent::class.java)
        go.remove(BoundsComponent::class.java)
        val pickable = ModelRenderable(ModelInstance(asset), go)
        go.add(RenderableComponent(pickable, go))
        go.add(PickableComponent(pickable, go))
        go.add(MaterialsComponent(asset.materials, go))
        go.add(MeshComponent(asset.meshes, go))
        go.add(BoundsComponent(BoundsComponent.getBounds(asset),go))
    }

    object Factory {
        fun fromPrimitive(params: ShapeParameters, go: GameObject, manager: EditorAssetManager<*,*>) : ModelComponent {
            val handle = manager.createHandle(Gdx.files.absolute(ShapeParameters.generatePath(inject(),params)))
            val primModel : PrimitiveModel = Primitive.fromParams(params)
            val asset = ModelAsset(params.kind,handle)
            val cmp =  ModelComponent(handle,go).apply {
                this.asset = asset
                this.exportState["type"] = "primitive"
                this.exportState["params"] = yaml.encodeToString(ShapeParameters.serializer(),params)
            }
            return cmp
        }
        fun fromFile(handle: AssetHandle, go: GameObject) : ModelComponent {
            val cmp =  ModelComponent(handle,go).apply {
                this.exportState["type"] = "file"
            }
            return cmp
        }
    }

}