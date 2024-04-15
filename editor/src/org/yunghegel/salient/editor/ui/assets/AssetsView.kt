package org.yunghegel.salient.editor.ui.assets

import com.badlogic.gdx.utils.Align
import org.yunghegel.gdx.utils.ext.padVertical
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.scene2d.STable

class AssetsView: STable() {

    private val assetManager : AssetManager = inject()
    private val sceneAssetsList = SceneAssetsList(inject(),inject(),assetManager)
    private val projectAssetsList = ProjectAssetsList(inject(),assetManager,sceneAssetsList)


    init {
        pad(4f)
        align(Align.top)
        add(projectAssetsList).growX().padVertical(5f)
        row()
        add(sceneAssetsList).growX().padVertical(5f)

    }





}