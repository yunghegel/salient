package org.yunghegel.salient.engine.ui.scene2d

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import org.yunghegel.salient.engine.ui.UI

open class SSelectBox<T>() : SelectBox<T>(UI.skin) {

    val names : MutableList<String> = mutableListOf()

    var nameMap : (T) -> String = { it.toString() }

    constructor(nameMap: (T) -> String) : this() {
        this.nameMap = nameMap
    }

    override fun setItems(vararg newItems: T) {
        names.clear()
        names.addAll(newItems.map { nameMap(it) })
        super.setItems(*newItems)

    }

    override fun toString(item: T): String {
        return nameMap(item)
    }

}