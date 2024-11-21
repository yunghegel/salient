package org.yunghegel.salient.engine.ui.layout

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import ktx.actors.onClick
import org.yunghegel.gdx.utils.ext.defaults
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SWindow
import org.yunghegel.salient.engine.ui.table

class PanelWindow(title :String, icon: String)  : Window("", UI.skin,"custom") {

    val closeButton = SImageButton("close")

    val content = Panel()
    val scrollTable = table()

    init {
        with(content) {
            createIcon(icon)
            createTitle(title)
            createTitleActor(closeButton) {
                it.right()
                it.padHorizontal(5f)
                it.height(18f)
            }
        }
        super.add(content).grow().minWidth(200f).minHeight(200f)
        removeActor(titleTable)
        configListeners()
        createScrollPane {  }
    }

    override fun add(vararg actors: Actor?): Table {
        return scrollTable.add(*actors)
    }

    fun configListeners() {
        closeButton.onClick {
            remove()
            UI.touchFocusActor = null
        }
    }

    fun show(x: Float, y: Float) {
        UI.root.addActor(this)
        setPosition(x, y)
        UI.touchFocusActor = this
    }



    fun createScrollPane(conf: Table.()->Unit) {
        content.bodyTable.clear()

        val scrollpane = ScrollPane(scrollTable)
        scrollpane.defaults()
        scrollTable.conf()
        content.bodyTable.add(scrollpane).grow()

    }



}