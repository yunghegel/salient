package org.yunghegel.salient.engine.ui.widgets.viewport

import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuBar
import com.kotcrab.vis.ui.widget.MenuItem
import ktx.actors.onClick
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.widgets.InputResult
import org.yunghegel.salient.engine.ui.widgets.Result
import org.yunghegel.salient.engine.ui.widgets.notif.SToast

class ViewportMenu : MenuBar("no-bg") {

    val view = ViewportMenuItem("View")
    val add = ViewportMenuItem("Add")
    val select = ViewportMenuItem("Select")
    val obj = ViewportMenuItem("Object")

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

    inner class ViewportMenuItem(name: String) : Menu(name, "viewport") {

        fun define(name: String, icon: String, toast: SToast? = null, handleResult: (InputResult) -> Unit) {
            val drawable : Drawable = UI.skin.getDrawable(icon)
            val item = MenuItem(name,drawable)
            toast?.let { notif ->
                notif.useResult = handleResult
                item.onClick {
                    UI.notifications?.push(notif)
                }
            }
            addItem(item)

        }


    }

}