package org.yunghegel.salient.editor.ui.assets

import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.asset.EditorAssetManager
import org.yunghegel.salient.engine.api.asset.type.MaterialAsset
import org.yunghegel.salient.engine.api.asset.type.ModelAsset
import org.yunghegel.salient.engine.api.asset.type.TextureAsset
import org.yunghegel.salient.engine.system.inject

object AssetPreview {

    val assets: EditorAssetManager<*, *> = inject()

    fun showAssetPreview(asset: Asset<*>) {
        val scene: Scene = inject()
        when (asset) {
            is ModelAsset -> showModelPreview(asset, scene)
            is TextureAsset -> showTexturePreview(asset)
            is MaterialAsset -> showMaterialPreview(asset)
        }
    }

    fun showModelPreview(model: ModelAsset, scene: Scene) {


    }

    fun showTexturePreview(texture: TextureAsset) {

    }

    fun showMaterialPreview(material: MaterialAsset) {

    }

    class ModelPreviewContext {

    }


}