package org.yunghegel.salient.engine.ui.widgets.menu

import com.kotcrab.vis.ui.widget.MenuItem

class TypedMenuItem<T>(val value: T) : ContextMenu() {

    var result: T? = null

    fun addMenuItem(name: String, handle: (T)->Unit) {
        addOption(name,null) { handle(value) }
    }

    fun <T,R> addMenuWithResult(name:String,handle: (T)->R) {
        val menu = MenuItem(name)

    }




}