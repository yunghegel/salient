package org.yunghegel.salient.editor.ui.assets.browser

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Selection
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.layout.GridGroup
import com.kotcrab.vis.ui.widget.Separator
import com.kotcrab.vis.ui.widget.VisSplitPane
import ktx.actors.onChange
import ktx.actors.onTouchEvent
import ktx.collections.toGdxArray
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.gdx.utils.ext.padVertical
import org.yunghegel.gdx.utils.ui.search.ResultsContainer
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.asset.type.AssetType
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SList
import org.yunghegel.salient.engine.ui.scene2d.SSelectBox
import org.yunghegel.salient.engine.ui.table

class AssetsContainer(skin : Skin = UI.skin, val browser: AssetBrowser) : ResultsContainer<Asset<*>,AssetActor>(skin,browser) {

    private val grid : GridGroup = GridGroup()

    internal val left = table()
    internal val right = table()
    internal val head = table()
    private val split = VisSplitPane(left,right,false)

    val list  = SList<String>()
    val category = SSelectBox<String>()

    private val zoomIn = SImageButton("zoom_in")
    private val zoomOut = SImageButton("zoom_out")

    fun getAssetActors() : kotlin.collections.List<AssetActor>  {
        val res = mutableListOf<AssetActor>()
        grid.children.forEach {
            if (it is AssetActor) res.add(it)
        }
        return res
    }

    var size = 100f
        set(value) {
            field = value
            grid.setItemSize(value)
        }

    init {

        grid.setItemSize(size)
        grid.spacing = 15f

        initright()
        initleft()
        inithead()
        add(head).growX().row()
        add(Separator()).growX().padVertical(2f).row()
        add(split).grow()
        split.setSplitAmount(.2f)

        touchable = Touchable.enabled

        onTouchEvent { event, x, y, pointer, button ->
            if (event.type== InputEvent.Type.touchDown) {
                val res = hit(x,y,true)
                getAssetActors().forEach { actor ->
                    if (res == actor || res.isDescendantOf(actor)) {
                        actor.select(selection)
                    } else if (actor.selected) {
                        actor.deselect(selection)
                    }
                }
                if (!res::class.java.isAssignableFrom(AssetActor::class.java)) {
                    selection.clear()
                }
            }
        }



    }

    val selection : Selection<AssetActor> = Selection()

    val filterSelection = { list : kotlin.collections.List<Asset<*>> ->
        if (category.selected == "All") {
            list
        } else {
            list.filter { it.assetType == AssetType.valueOf(category.selected) }
        }
    }





    @OptIn(ExperimentalStdlibApi::class)
    fun initleft() {

        val pane = ScrollPane(list).apply { setScrollingDisabled(true,false); setScrollbarsVisible(true); setOverscroll(false,false); setFlingTime(0f)}
        left.add(table{
            add(pane).grow().minWidth(100f)
        }).grow()
    }

    fun initright() {
        val pane = ScrollPane(grid).apply { setScrollingDisabled(true,false); setScrollbarsVisible(true); setOverscroll(false,false); setFlingTime(0f); setForceScroll(false,true) }
        right.add(pane).grow()
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun inithead(){
        category.items = (AssetType.entries.map { it.name}.toMutableList() + "All").toGdxArray()
        category.selected = "All"
        category.onChange {
            browser.searchBar.searchResults = filterSelection(browser.searchBar.searchResults)
        }
        head.align(Align.left)
        head.add(category).pad(4f)

        zoomIn.onChange {
            size *= 1.1f
        }
        zoomOut.onChange {
            size *= .9f
        }

        val zoomControls = table {
            add(zoomIn).padHorizontal(5f).size(18f)
            add(zoomOut).padHorizontal(5f).size(18f)
        }
        head.add(zoomControls).pad(4f)
    }

    override fun resetContainer() {
       grid.clearChildren()
    }

    override fun addResult(actor: AssetActor) {
        grid.addActor(actor)
    }
}