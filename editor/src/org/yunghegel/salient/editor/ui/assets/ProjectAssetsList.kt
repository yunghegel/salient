package org.yunghegel.salient.editor.ui.assets

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import org.yunghegel.gdx.utils.ui.ActorList
import org.yunghegel.gdx.utils.ui.LabelSupplier
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.engine.api.asset.type.AssetType
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.events.asset.onAssetIndexed
import org.yunghegel.salient.engine.events.asset.onAssetUnindexed
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.layout.CollapsePanel
import org.yunghegel.salient.engine.ui.scene2d.*
import org.yunghegel.salient.engine.ui.widgets.list.InteractiveList

class ProjectAssetsList(val project: Project, val manager: AssetManager, val sceneAssetsList: SceneAssetsList

) : CollapsePanel("Project Index",null,null) {

    val list = ActorList(UI.skin)
    val importAsset = STextButton("Import Assets")

    val interactiveList = object : InteractiveList<AssetHandle,AssetManager>({ manager }) {

        override val controller = object : Controller<AssetHandle,AssetManager> {
            override fun create(): AssetHandle {
                return AssetHandle()
            }

            override fun plus(item: AssetHandle) {

            }

            override fun minus(item: AssetHandle) {

            }

            override fun copy(item: AssetHandle): AssetHandle {
              return AssetHandle(item.path.toString())

            }

            override fun selected(item: AssetHandle?,applyTo:AssetManager?) {

            }
        }

        override fun itemToString(item: AssetHandle): String {
            return item.name
        }

        override fun createRow(item: AssetHandle): Row {
            val row =object: Row(item) {

                val delete = SImageButton("delete")
                val inspect = SImageButton("inspect_file")
                val addToScene = STextButton("+scene")

                val type = AssetType.fromFile(item.path.handle)




                override fun buildActor() {
                    if(type!=null) {
                        val drawable = AssetType.iconFor(type)
                        add(SImage(drawable, 16)).size(16f).padLeft(5f)
                    }

                    add(label).growX()


                    add(addToScene)
                    addToScene.onChange {
                        manager.includeAsset(item, project.currentScene!!)
                        sceneAssetsList.list.setItems(project.currentScene!!.retrieveAssetIndex().map { sceneAssetsList.AssetTable(it) }.toTypedArray())
                    }
                }
            }

            return row
        }


    }

    init {
        align(Align.top)
        createTitle("Project Assets")
        createTitleActor(importAsset)
        contentContainer.actor = interactiveList

        project.assetIndex.forEach { indexAsset(it);}
        buildListeners()
    }

    private fun buildListeners() {
        onAssetIndexed { event ->
            list.setItems(project.assetIndex.map { AssetTable(it) }.toTypedArray()) {
            }
            interactiveList.addItem(event.handle)
        }
        onAssetUnindexed { event ->
            list.items.forEach {
                if (it is AssetTable && it.handle == event.handle) {
                    list.remove(it)
                }
            }
            interactiveList.remove(event.handle)
        }
        importAsset.addListener(object :ChangeListener(){
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                AssetDialog(manager).indexNewAssets()
            }

        })
    }

    private fun indexAsset(handle: AssetHandle) {
        val table = AssetTable(handle)
        list.addItem(table)
        interactiveList.addItem(handle)
    }



    inner class AssetTable(val handle: AssetHandle, ) : STable(), LabelSupplier {

        override var label: String? = handle.name

        val delete = SImageButton("delete")
        val inspect = SImageButton("inspect_file")
        val addToScene = STextButton("+scene")

        init {
            add(delete).size(18f).padRight(4f)
            add(inspect).size(18f)
            add(addToScene)
            addToScene.onChange {
                manager.includeAsset(handle, project.currentScene!!)
                sceneAssetsList.list.setItems(project.currentScene!!.retrieveAssetIndex().map { sceneAssetsList.AssetTable(it) }.toTypedArray())
            }
        }

    }

}