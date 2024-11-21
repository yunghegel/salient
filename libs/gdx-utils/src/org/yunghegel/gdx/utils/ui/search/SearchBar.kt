package org.yunghegel.gdx.utils.ui.search

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import ktx.scene2d.Scene2DSkin
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.gdx.utils.ui.IconTextfield

class SearchBar<T, A: Actor,C:ResultsContainer<T,A>>(skin: Skin = Scene2DSkin.defaultSkin, private val results :C, private val manager : SearchManager<T,A,C>) : Table(skin) {


    val popup : PopTable = PopTable(skin)

    val actorcache: MutableMap<T,A> = mutableMapOf()

    var input : String = ""
        get() {
            return textfield.input ?: ""
        }
        set(value) {
            if (value.isEmpty() || value.isBlank()) showAll()
            searchResults = manager.search(value, manager.getCandidates())
            field = value
        }

    val textfield : IconTextfield = IconTextfield(IconTextfield.Option.LEFT, skin, "FindAll", "")

    var searchResults : List<T> = mutableListOf()
        set(value) {
            field = value
            val actors = filter(searchResults).map { res ->getActor(res) }
            results.update(actors)
            manager.resultsChanged(value)
        }


    init {
        build()
        createListeners()
        showAll()
    }

    fun getActor(item: T) : A{
        return actorcache.getOrPut(item) { manager.transform(item) }
    }

    private fun build() {
        add(textfield).growX()
        popup.isHideOnUnfocus = true
        popup.attachToActor(textfield, Align.bottomRight, Align.bottomLeft)
        popup.attachOffsetY=-10f

        popup.add(results).grow()
    }

    fun filter(items: List<T>) : List<T> {
        val res = mutableListOf<T>()
        items.each { item ->
            if (manager.keep(item)) res.add(item)
        }
        return res
        }


     fun showAll() {
        results.update(  manager.getCandidates().map { getActor(it) })
    }

    fun show(list: List<T>) {
        results.update(list.map { getActor(it)})
    }



    private fun createListeners(){
        textfield.listen {
            onTextChange { text ->
               input = text
            }
            onLeftIconClick {
                if (popup.isHidden) popup.show(stage)
                else popup.hide()
            }
        }
    }


}