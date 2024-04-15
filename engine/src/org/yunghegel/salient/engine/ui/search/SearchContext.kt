package org.yunghegel.salient.engine.ui.search

import org.yunghegel.gdx.utils.data.Searchable

fun interface SearchMapper<T> {

    fun map(searchable: Searchable): T

}

class SearchContext<T: Searchable> {

    val searchables = mutableListOf<T>()

    var currentScope = "global"



}