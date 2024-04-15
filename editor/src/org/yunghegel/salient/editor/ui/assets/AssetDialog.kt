package org.yunghegel.salient.editor.ui.assets

import ktx.collections.GdxArray
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.engine.api.asset.type.AssetType
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.gdx.utils.ui.openFiles

class AssetDialog(val assetManager: AssetManager) {

    fun indexNewAssets() : GdxArray<AssetHandle> {
        val assets = GdxArray<AssetHandle>()
        openFiles(
            "Select assets to index",
            filterPatterns = AssetType.allExt,
            filterDescription = "Select assets to import") { file ->
            val handle = assetManager.createHandle(file)
            assetManager.indexHandle(handle, inject())
        }
        return assets
    }


}