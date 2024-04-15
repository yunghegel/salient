package org.yunghegel.salient.editor.ui.assets

import com.badlogic.gdx.utils.Align
import org.yunghegel.gdx.utils.ui.ActorList
import org.yunghegel.gdx.utils.ui.LabelSupplier
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.events.asset.onAssetIndexed
import org.yunghegel.salient.engine.events.asset.onAssetUnindexed
import org.yunghegel.salient.engine.events.scene.onSceneInitialized
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.layout.CollapsePanel
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.STable

class SceneAssetsList(val sceneManager: SceneManager, val scene: Scene, val assetManager: AssetManager) : CollapsePanel("Scene Assets") {

    val list = ActorList(UI.skin)

    init {
        align(Align.top)
        createTitle("Scene Assets")
        contentContainer.actor = list

        buildListeners()


    }

    private fun buildListeners() {
        onAssetIndexed { event ->
            list.setItems(scene.retrieveAssetIndex().map { AssetTable(it) }.toTypedArray()) {
            }
        }
        onAssetUnindexed { event ->
            list.items.forEach {
                if (it is AssetTable && it.handle == event.handle) {
                    list.remove(it)
                }
            }
        }
        onSceneInitialized {
            scene.retrieveAssetIndex().forEach { indexAsset(it) }
        }
    }

    private fun indexAsset(handle: AssetHandle) {
        val table = AssetTable(handle)
        list.addItem(table)
    }



    inner class AssetTable(val handle: AssetHandle, ) : STable(), LabelSupplier {

        override var label: String? = handle.name

        val delete = SImageButton("delete")
        val inspect = SImageButton("inspect_file")

        init {
            add(delete).size(18f).padRight(4f)
            add(inspect).size(18f)
        }

    }
}