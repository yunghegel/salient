package org.yunghegel.salient.editor.ui.assets

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import ktx.actors.onChange
import ktx.collections.toGdxArray
import mobx.core.autorun
import org.yunghegel.gdx.utils.ui.ActorList
import org.yunghegel.salient.editor.app.Gui
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.editor.ui.assets.browser.AssetActor
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.asset.type.AssetType
import org.yunghegel.salient.engine.system.file.FileType
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.child
import org.yunghegel.salient.engine.ui.layout.EditorFrame
import org.yunghegel.salient.engine.ui.scene2d.SSelectBox
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.search.SearchBar
import org.yunghegel.salient.engine.ui.table

class AssetBrowser : STable(), SearchBar.SearchProvider<Asset<*>, AssetActor> {

    val assetmanager : AssetManager = inject()

    val scenemanager : SceneManager = inject()

    val searchBar : SearchBar<Asset<*>,AssetActor> = SearchBar(this)

    val gui : Gui by lazy { inject() }

    val list : com.badlogic.gdx.scenes.scene2d.ui.List<String> = com.badlogic.gdx.scenes.scene2d.ui.List(UI.skin)

    var results = mutableListOf<Asset<*>>()

    @OptIn(ExperimentalStdlibApi::class)
    val selectBox = SSelectBox<String>().apply {
        items = (AssetType.entries.map { it.name}.toMutableList() + "All").toGdxArray()
        selected = "All"
        onChange {
            setItems(filterSelection(results))
        }
    }

    val currentFilter : String
        get() = selectBox.selected

    val filterSelection = { list : List<Asset<*>> ->
        if (currentFilter == "All") {
            list
        } else {
            list.filter { it.assetType == FileType.valueOf(currentFilter) }
        }
    }

    init {
        val container = table {
            child {
                add(selectBox).growX().pad(5f).height(20f).row()
                add(list).growY().minWidth(200f)
            }.grow()
            add(searchBar.results.list).grow()
        }
        val pane = ScrollPane(container)
        add(pane).grow()

        autorun {
            println(searchBar.search.textField.text)
        }

    }

    fun setItems(list : List<Asset<*>>) {
        val items = list.map { it.handle.name }
        this.list.setItems(*items.toTypedArray())
    }

    fun setAllItems() {
        val items = scenemanager.project.currentScene!!.assets.map { it.handle.name }
        this.list.setItems(*items.toTypedArray())
    }

    fun EditorFrame.PanelGroup.hookTitlebar() {
        customizeHeader { header , content->
            if (content.title == "Asset Browser") {
                header.add(searchBar).growX()
            }
        }
    }

    override val term: () -> String = {
        searchBar.search.textField.text ?: ""
    }

    override fun rebuildList(list: ActorList, searchables: List<Asset<*>>) {
        searchables.forEach {
            list.add(AssetActor(it))
        }
    }

    override fun search(query: String, searchables: List<Asset<*>>): List<Asset<*>> {
        val list = mutableListOf<Asset<*>>()


        searchables.forEach { asset ->
            for (term in asset.searchTerms) {
                if (term.contains(query)) {
                    list.add(asset)
                    break
                }
            }
        }
        list.forEach { println(it.handle.name) }
        val res = if (query.isEmpty())  searchables else list
        return filterSelection(res)
    }

    override fun collectSearchable(): List<Asset<*>> {
        return scenemanager.project.currentScene!!.assets
    }

    override fun processResults(results: List<Asset<*>>) {
        this.results = results.toMutableList()
        setItems(results)
    }


}