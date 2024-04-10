package org.yunghegel.salient.engine.ui.widgets.viewport

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuBar

class ViewportMenu : MenuBar("no-bg") {

    val view = (Menu("View", "viewport"))
    val add = (Menu("Add", "viewport"))
    val select = (Menu("Select", "viewport"))
    val obj = (Menu("Object", "viewport"))

    init {
        addMenu(view)
        addMenu(add)
        addMenu(select)
        addMenu(obj)

        val items = table.getChild(0) as Table
        items.defaults()

        items.cells.forEach {
            it.pad(0f, 5f, 0f, 5f)
            it.minWidth(50f)
        }
    }

}