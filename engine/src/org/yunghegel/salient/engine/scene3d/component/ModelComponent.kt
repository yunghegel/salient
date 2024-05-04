package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.graphics.g3d.Model
import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.salient.engine.api.asset.type.ModelAsset
import org.yunghegel.salient.engine.api.dto.component.ModelComponentDTO
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.graphics.shapes.Primitive
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.system.warn


class ModelComponent(model: ID, go: GameObject, modelAsset: ModelAsset? = null) : EntityComponent<Model>(modelAsset?.value,go) {

    constructor(model:ModelAsset,go: GameObject) : this(model.handle,go) {
        this.value = model.value
        model.useAsset(model.value!!,go)
    }



    val meta = Meta(model)

    var usedAsset : ModelAsset? = modelAsset
        set(value) {
            if (value!=null && field != value) {
                field = value
                this.value = value.value
                value.useAsset(value.value!!,go)
            }
        }

    init {
        if(modelAsset==null) {
            recoverState(model)
                .onFailure { warn(it.message?: "Failure to recover model component state") }
                .onSuccess { info("Restored state for component of ${go.id} from ${model.id}") }
        } else {
            applyAsset(modelAsset)
        }
        if(go.getComponent(ModelComponent::class.java)==null) println("Model component added to ${go.name}")
        go.add(this)


    }

    private fun recoverState(accessor: ID) : Result<Boolean> {
        if (meta.strategy== RetrievalStrategy.FILE) {
            val scene : EditorScene = go.scene
            scene.assets.forEach { asset ->
                if(accessor.uuid == asset.handle.uuid) {
                    if (asset is ModelAsset) {
                        applyAsset(asset)
                        return Result.success(true)
                    }
                }
            }
        }
        return Result.failure(Exception("Could not recover state"))
    }
    fun applyAsset(asset: ModelAsset) {
        usedAsset = asset
        if(!asset.loaded) asset.load()
        this.value = asset.value
        asset.useAsset(asset.value!!,go)

    }

    class Meta(val identifier: ID) {

        val id: ID = identifier

        val strategy: RetrievalStrategy = when(identifier) {
            is Primitive -> RetrievalStrategy.PRIMITIVE
            else -> RetrievalStrategy.FILE
        }

        var retrieval : String = when(identifier) {
            is AssetHandle -> identifier.path.path
            is Primitive -> identifier.supplier.simpleName!!
            else -> "<error>"
        }

    }

    enum class RetrievalStrategy {
        FILE,
        PRIMITIVE
    }

    companion object {
        fun toDTO(model: ModelComponent, go: GameObject) : ModelComponentDTO {
            return ModelComponentDTO().apply {
                id = model.meta.id.id
                uuid = model.meta.id.uuid
                strategy = model.meta.strategy
                retrieval = model.meta.retrieval
            }
        }

        fun fromDTO(dto: ModelComponentDTO,scene: EditorScene,gameObject: GameObject) : ModelComponent? {
            if (dto.strategy== RetrievalStrategy.FILE) {
                val handle = scene.retrieveAssetIndex().find { it.uuid == dto.uuid } as AssetHandle?
                if (handle != null) return ModelComponent(handle,gameObject)
            }
            return null
        }
    }

}