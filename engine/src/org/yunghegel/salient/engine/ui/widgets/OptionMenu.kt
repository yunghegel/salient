package org.yunghegel.salient.engine.ui.widgets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import ktx.actors.onChange
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SCheckBox

class OptionMenu : PopupMenu() {

    val result = Result()

    var submit : (Result)->Unit = {}

    fun menu(name: String, action: () -> Unit) {
        addItem(makeItem(name, action))
    }

    fun toggle(name: String, default: Boolean = false, action: (Boolean) -> Unit) {
        val item = MenuItem(name)
        val label = item.label
        result[name] = default
        item.clearChildren()
        item.add(SCheckBox(name,default).apply {
            onChange {
                action(this.isChecked)
                result[name] = this.isChecked
            }
        })
        addItem(item)
    }

    fun withResults(action: (Result) -> Unit) {
        submit = action
    }

    fun separator() {
        addSeparator()
    }

    private fun makeItem(name: String, action: () -> Unit) : MenuItem {
       val item = MenuItem(name)
         item.onChange {
              action()
         }
        return item
    }

    fun show() : Result {
        return show(UI, Gdx.input.x, Gdx.graphics.height- Gdx.input.y)

    }

    fun show(stage: Stage, x:Int, y:Int) : Result {
        showMenu(stage, x.toFloat(), y.toFloat())
        return result
    }

    override fun remove(): Boolean {
        submit(result)
        return super.remove()
    }

    companion object {
        fun show(stage: Stage, x:Int, y:Int, conf: OptionMenu.() -> Unit) : Result {
            val menu = OptionMenu()
            menu.conf()
            return menu.show(stage, x, y)
        }
    }
}