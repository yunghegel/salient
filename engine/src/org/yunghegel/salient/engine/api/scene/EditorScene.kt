package org.yunghegel.salient.engine.api.scene

import com.badlogic.gdx.utils.Disposable
import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.gdx.utils.data.Mask
import org.yunghegel.salient.engine.api.*
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.api.properties.NamedObjectResource
import org.yunghegel.salient.engine.helpers.Ignore
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.SceneContext
import org.yunghegel.salient.engine.system.info

abstract class EditorScene(val ref: SceneHandle) : UpdateRoutine, NamedObjectResource,

    RendererRoutine, ResizeRoutine, Disposable, Mask {


    override var mask: Int = 0

    val index : MutableList<AssetHandle> = mutableListOf()

    @Ignore
    val assets : MutableList<Asset<*>> = mutableListOf()

    @Ignore
    abstract val renderer: EditorSceneRenderer<*,*>

    @Ignore
    abstract val graph: EditorSceneGraph

    @Ignore
    abstract val context: SceneContext


    @Ignore
    abstract val selection : BaseSelectionManager<GameObject>

    fun indexAsset(asset: AssetHandle) {
        index.forEach { if (it.uuid == asset.uuid) return }
        info("Indexed asset ${asset.name} for scene ${ref.name}")
        index.add(asset)
    }

    fun retrieveAssetIndex() : List<AssetHandle> {
        return index
    }

    fun findAsset(handle : ID) : Asset<*>? {
        return assets.find { it.handle == handle }
    }

    val initialized : Boolean
        get() = allOf(ASSETS_INITIALIZED,SCENE_CONTEXT_INITIALIZED,SCENE_GRAPH_INITIALIZED)

    companion object {

        val EditorScene.folder get() = ref.file

        const val ASSETS_INITIALIZED = 1
        const val SCENE_CONTEXT_INITIALIZED = 2
        const val SCENE_GRAPH_INITIALIZED = 4
        const val SCENE_ACTIVE = 8
    }






}