package org.yunghegel.gdx.utils.ui.search

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.ray3k.stripe.GridDrawable
import ktx.actors.onChange
import org.yunghegel.gdx.utils.ext.each

abstract class ResultsContainer<T,Item: Actor>(skin:Skin, val manager: SearchManager<T,Item,*>) : Table(skin) {

    abstract fun resetContainer()

    abstract fun addResult(actor: Item)

    init {
        onChange {
            println("change")
            updateFiltered(manager.getCandidates())
        }
    }

    fun update(items : List<Item>) {
        resetContainer()
        items.each { item -> addResult(item) }
    }
    fun updateFiltered(items: List<T>) {
        update(
        items.filter {
            manager.filter(manager.input() ?: "", it)
        }.map { manager.searchBar.getActor(it) }
        )

    }

    inner class Builder {

        private var reset: (()->Unit)?=null
        private var add: ((Item)->Unit)?=null

        fun reset(action: ()->Unit) {
            this.reset = action
        }

        fun add(action:(Item)->Unit) {
            this.add = action
        }

    }



}