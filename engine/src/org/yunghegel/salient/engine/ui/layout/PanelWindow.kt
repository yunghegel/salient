package org.yunghegel.salient.engine.ui.layout

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import org.yunghegel.gdx.utils.ext.defaults
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SWindow
import org.yunghegel.salient.engine.ui.table

class PanelWindow(title :String, icon: String)  : Window("", UI.skin,"custom") {

    val closeButton = SImageButton("close")

    val content = Panel()

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
        add(content).grow()
        removeActor(titleTable)
    }

    fun createScrollPane(conf: Table.()->Unit) {
        content.bodyTable.clear()
        val table = table()
        val scrollpane = ScrollPane(table)
        scrollpane.defaults()
        table.conf()
        content.bodyTable.add(scrollpane).grow()

    }



}