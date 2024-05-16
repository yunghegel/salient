package org.yunghegel.salient.engine.ui.search

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import org.yunghegel.gdx.utils.data.Searchable
import org.yunghegel.gdx.utils.ui.ActorList
import org.yunghegel.gdx.utils.ui.LabelSupplier
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.gdx.utils.ui.IconTextfield
import org.yunghegel.salient.engine.ui.layout.Panel

class SearchBar<T: Searchable,A>(searchProvider: SearchProvider<T,A>) : STable() where A : LabelSupplier, A : Actor {

    val search = IconTextfield(IconTextfield.Option.LEFT,skin, "FindAll", "")

    var searchContext = SearchContext<T>()

    val resultsContainer = Panel()
    val results = SearchResults(searchProvider)


    val popup  = PopTable()
    init {
        add(search).growX()
        popup.isHideOnUnfocus = true
        popup.attachToActor(search, Align.bottomRight,Align.bottomLeft)
        popup.attachOffsetY=-10f

        val searchListener = object : IconTextfield.IconTextfieldListener {
            override fun onLeftIconClick() {
                if (popup.isHidden) popup.show(stage)
                else popup.hide()
            }

            override fun onRightIconClick() {
            }

            override fun onTextChange(text: String) {
                results.update()
                if(text.isEmpty() or text.isBlank()) cancelSearch()
                else if (popup.isHidden) popup.show(stage)
            }
        }

        search.setListener(searchListener)
        results.updateSearchales()
        resultsContainer.add(results).grow()
        resultsContainer.titleTable.background = UI.drawable("tab_down", Color(0.3f,0.3f,0.3f,1f))
        resultsContainer.bodyTable.skin= UI.skin
        resultsContainer.bodyTable.background = UI.drawable("tab_panel", Color(0.4f,0.4f,0.4f,1f))
        popup.add(resultsContainer).grow()

        addListener(object:InputListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
//                if(hit(x,y,false) == null) cancelSearch()
                return super.touchDown(event, x, y, pointer, button)
            }

        })
        addListener(object:FocusListener(){
            override fun keyboardFocusChanged(event: FocusEvent?, actor: Actor?, focused: Boolean) {
                if(!actor?.isDescendantOf(popup)!!) search.textField.text = ""

                super.keyboardFocusChanged(event, actor, focused)
            }
        })
    }

    fun cancelSearch() {
        popup.hide()
    }



    inner class SearchResults(private val provider: SearchProvider<T, A>) : STable() {



        val list: ActorList

        private var searchables = provider.collectSearchable()

        init {

            val listStyle = ActorList.ActorListStyle()

            list = object :  ActorList(UI.skin,listStyle) {
                override fun getPrefWidth(): Float {
                    return this@SearchBar.width
                }
            }
            add(list).grow()
        }

        internal fun updateSearchales(){
            searchables = provider.collectSearchable()
        }

        fun update() {
            if (searchables.isEmpty()) updateSearchales()
            var items = provider.search(provider.term(),searchables)
            items = sortAlphabetically(items)
            provider.rebuildList(list,items)
            provider.processResults(items)
        }

        inline fun < reified A : Actor> updateItems (items: List<T>, transformer: (T) -> A) {
            val actors = items.map { transformer(it) }
            list.items.clear()
            list.setItems(actors.toTypedArray()) {
                height(20f)
                growX()
            }

        }

        private fun sortAlphabetically(arrayList: List< T >): List< T >{
            return arrayList.sortedBy { it.searchTerms.joinToString() }
        }

        override fun getMinWidth(): Float {
            return this@SearchBar.width
        }

    }

    interface SearchProvider<T: Searchable,A> where A : Actor, A : LabelSupplier{

        val term: ()->String

        fun search(query: String,searchables: List<T> = collectSearchable()): List<T>

        fun rebuildList(list: ActorList, searchables: List<T>)

        fun collectSearchable(): List<T>

        open fun processResults(results: List<T>) {}

    }

}