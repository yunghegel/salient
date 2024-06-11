package org.yunghegel.salient.engine.ui.widgets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.kotcrab.vis.ui.widget.MenuItem
import com.kotcrab.vis.ui.widget.PopupMenu
import ktx.actors.onChange
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SCheckBox

class OptionMenu(private var cellConf : ((Cell<*>)->Unit)? = null) : PopupMenu() {

    interface Option {
        val name: String
        val icon : String?
        val action: () -> Unit
    }

    val items = mutableListOf<Option>()

    private fun createMenu(option: Option, imageConfigurator: ((Cell<*>)->Unit)?=null) : MenuItem {
        if (option.icon != null) {
            val item = MenuItem(option.name, UI.drawable(option.icon!!))
            if (imageConfigurator != null) {
                item.imageCell?.let { imageConfigurator(item.imageCell) }
            }
            item.onChange { option.action() }
            return item
        } else {
            val item = MenuItem(option.name)
            item.onChange { option.action() }
            return item
        }
    }

    fun addOption(option: Option) {
        items.add(option)
        addItem(createMenu(option, cellConf))
    }

    fun addOption(name: String, icon: String?, action: () -> Unit) {
        addOption(option(name, icon, action))
    }



    class Builder {
        private val menu = OptionMenu()
        fun option(name: String, icon: String?, action: () -> Unit) {
            menu.addOption(name, icon, action)
        }
        fun build() : OptionMenu {
            return menu
        }
    }

    companion object {
        fun option(name: String, icon: String?, action: () -> Unit) : Option {
            return object : Option {
                override val name: String = name
                override val icon: String? = icon
                override val action: () -> Unit = action
            }
        }

        fun build(init: Builder.() -> Unit) : OptionMenu {
            val builder = Builder()
            builder.init()
            return builder.build()
        }
    }


}