package org.yunghegel.salient.editor.ui.assets

import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.engine.io.inject
import org.yunghegel.salient.engine.ui.scene2d.STable

class AssetsView: STable() {

    val assetManager : AssetManager = inject()

    init {

    }

}