package org.yunghegel.salient.engine.api.scene

import com.badlogic.gdx.utils.Disposable
import org.yunghegel.gdx.utils.data.Mask
import org.yunghegel.salient.engine.api.EditorSceneManager
import org.yunghegel.salient.engine.api.RendererRoutine
import org.yunghegel.salient.engine.api.ResizeRoutine
import org.yunghegel.salient.engine.api.UpdateRoutine
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.scene3d.SceneContext

abstract class EditorScene(val ref: SceneHandle, val sceneManager: EditorSceneManager<*>) : UpdateRoutine,
    RendererRoutine, ResizeRoutine, Disposable, Mask {

    override var mask: Int = 0

    private val assetIndex : MutableList<AssetHandle> = mutableListOf()

    val assets : MutableList<Asset<*>> = mutableListOf()

    abstract val renderer: EditorSceneRenderer<*,*>

    abstract val graph: EditorSceneGraph

    abstract val context: SceneContext

    fun indexAsset(asset: AssetHandle) {
        assetIndex.forEach { if (it.uuid == asset.uuid) return }
        assetIndex.add(asset)
    }

    fun retrieveAssetIndex() : List<AssetHandle> {
        return assetIndex
    }

    val initialized : Boolean
        get() = allOf(ASSETS_INITIALIZED,SCENE_CONTEXT_INITIALIZED,SCENE_GRAPH_INITIALIZED)

    companion object {
        const val ASSETS_INITIALIZED = 1
        const val SCENE_CONTEXT_INITIALIZED = 2
        const val SCENE_GRAPH_INITIALIZED = 4
        const val SCENE_ACTIVE = 8
    }






}