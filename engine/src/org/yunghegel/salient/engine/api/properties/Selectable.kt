package org.yunghegel.salient.engine.api.properties

import org.yunghegel.salient.engine.api.SelectionSource

interface Selectable {

    val sources : List<SelectionSource>

    val selected : Boolean

    fun setSelect(selected: Boolean)



}