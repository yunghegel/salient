package org.yunghegel.salient.engine.api.undo

import org.yunghegel.salient.engine.api.SelectionSource
import org.yunghegel.salient.engine.api.properties.Selectable

fun interface SelectionListener<T:Selectable> {

    fun onSelectionChanged(old: T?, new: T?, source: SelectionSource)

}