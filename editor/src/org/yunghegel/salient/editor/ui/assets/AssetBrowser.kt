package org.yunghegel.salient.editor.ui.assets

import org.yunghegel.gdx.utils.ui.ActorList
import org.yunghegel.salient.editor.app.Gui
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.editor.ui.assets.browser.AssetActor
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.layout.EditorFrame
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.search.SearchBar

class AssetBrowser : STable(), SearchBar.SearchProvider<Asset<*>, AssetActor> {

    val assetmanager : AssetManager = inject()

    val scenemanager : SceneManager = inject()

    val searchBar : SearchBar<Asset<*>,AssetActor> = SearchBar(this)

    val gui : Gui by lazy { inject() }

    init {

    }

    fun EditorFrame.PanelGroup.hookTitlebar() {
        customizeHeader { header ->
            header.add(searchBar).growX()
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
        return list
    }

    override fun collectSearchable(): List<Asset<*>> {
        return scenemanager.project.currentScene!!.assets
    }

    override fun processResults(results: List<Asset<*>>) {
        val actors = results.map { res -> AssetActor(res) }
        val totalWidth = gui.center.length
        var accRowWidth = 0f
        for (i in 0 until actors.size) {
            val actor = actors[i]
            val width = actor.prefWidth
            accRowWidth += width
            if (accRowWidth > totalWidth) {
                accRowWidth = width
                add(STable().apply {
                    add(actor).growX()
                }).growX().row()
            } else {
                add(actor).growX()
            }
        }
    }


}