package org.yunghegel.gdx.utils.ui.search

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import org.checkerframework.checker.units.qual.A

interface SearchManager<T, A: Actor, C: Table> {

    val container : C

    fun getCandidates() : List<T>

    val input: ()->String
        get() = {searchBar.input ?: ""}

    val searchBar: SearchBar<T, A,*>

    fun filter(query: String, item: T) : Boolean

    fun transform(item: T) : A

    fun search(query:String, candidates: List<T>) : List<T> {
        val results = mutableListOf<T>()
        candidates.forEach { item ->
            if (filter(query,item)) results.add(item)
        }
        results.forEach { println(it)}
        container.fire(ChangeEvent())
        return results
    }

    fun resultsChanged(results: List<T>)

    fun keep(item:T) : Boolean { return true}


}