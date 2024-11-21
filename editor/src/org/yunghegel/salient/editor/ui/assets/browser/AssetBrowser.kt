package org.yunghegel.salient.editor.ui.assets.browser

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload
import ktx.actors.onChange
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.gdx.utils.ext.ref
import org.yunghegel.gdx.utils.ui.search.SearchBar
import org.yunghegel.gdx.utils.ui.search.SearchManager
import org.yunghegel.salient.editor.app.Gui
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.plugins.picking.tools.HoverTool
import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.events.asset.onAssetIncluded
import org.yunghegel.salient.engine.system.debug
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.layout.EditorFrame
import org.yunghegel.salient.engine.ui.scene2d.STable

class AssetBrowser : STable(), SearchManager<Asset<*>,AssetActor,AssetsContainer> {

    private val assetmanager : AssetManager = inject()

    private val scenemanager : SceneManager = inject()

    override val container = AssetsContainer(skin, this)


    val hovertool : HoverTool by lazy  { inject() }

    val dnd = DragAndDrop()

    private val sources  = mutableMapOf<AssetActor,DragAndDrop.Source>()

    override val searchBar : SearchBar<Asset<*>,AssetActor,AssetsContainer> = SearchBar(skin,container,this)

    val gui : Gui by lazy { inject() }

    private val all : List<Asset<*>>
        get() = scenemanager.project.currentScene!!.assets


    private var results by ref(mutableListOf<Asset<*>>())







    init {
        add(container).grow()
        container.head.add(searchBar).growX().maxWidth(300f).right().padHorizontal(25f)
        setAllItems()

        onAssetIncluded { event ->
            val asset = assetmanager.locateAsset(event.asset)
            info("appending asset to browser view : ${asset?.handle}")
            asset?.let {
                val items = all.filter { filter(searchBar.textfield.input,it) }
                resultsChanged(items)
            }
            if (filter(searchBar.input,asset!!)) {container.addAsset(asset)
                debug("added asset to browser")
            }
            fire(ChangeEvent())
        }

        onChange {
            container.updateFiltered(all)
        }
    }

    fun hookDropTarget(target: Actor, shouldaccept: (Payload)->Boolean, onAccept: (Payload?)->Unit) {
        dnd.addTarget(object : DragAndDrop.Target(target){
            override fun drag(
                p0: DragAndDrop.Source?,
                p1: DragAndDrop.Payload?,
                p2: Float,
                p3: Float,
                p4: Int
            ): Boolean {
                if (p1 != null) {

                    return shouldaccept(p1)
                } else return false
            }

            override fun drop(p0: DragAndDrop.Source?, p1: DragAndDrop.Payload?, p2: Float, p3: Float, p4: Int) {
                onAccept(p1)
            }

        })
    }

    private fun setItems(list : List<Asset<*>>) {
        if (list.isEmpty()) {
            if(all.isNotEmpty()) setItems(all)
            return
        }
        val items = list.map { it.handle.name }
        container.list.setItems(items.toTypedArray())
    }

    private fun setAllItems() {
        val items = scenemanager.project.currentScene!!.assets.map { it }
        setItems(items)

    }

    override fun getCandidates(): List<Asset<*>> {
        return all
    }




    override fun resultsChanged(results: List<Asset<*>>) {
        container.list.setItems(results.map { it.handle.name }.toTypedArray())
    }

    override fun transform(item: Asset<*>): AssetActor {
        val actor = AssetActor(item)
        val source = actor.createDND(dnd)
        sources[actor] = source
        return actor
    }

    override fun filter(query: String, item: Asset<*>): Boolean {
        if ( container.category.selected == "All") return true
        val searchTerms = listOf(item.handle.name,item.assetType.name,item.handle.pth)
        if (query.isBlank()) return true
        return (searchTerms.any { term -> term.contains(query) && container.category.selected == item.assetType.name})
    }

    override fun keep(item : Asset<*>) : Boolean {
        return (item.assetType.name == container.category.selected || container.category.selected == "All")
    }







}