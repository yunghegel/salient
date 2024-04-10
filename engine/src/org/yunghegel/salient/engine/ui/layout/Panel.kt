package org.yunghegel.salient.ui.container

import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import org.yunghegel.salient.ui.scene2d.SImageButton
import org.yunghegel.salient.modules.ui.scene2d.SLabel
import org.yunghegel.salient.modules.ui.scene2d.STable
import org.yunghegel.salient.modules.ui.scene2d.STextButton


open class Panel(val title: String = "Default",val tabButtonStyle: String = "default") : STable() {
    var tabTable: STable
    var bodyTable: STable
    var tab: Tab = Tab(title)
    var plusButton: SImageButton = SImageButton("plus")


    init {
        align(Align.left)
        tabTable = STable()
        tabTable.add(tab).pad(0f)
        tabTable.align(Align.left)

        add(tabTable).growX().left()

        row()
        bodyTable = STable()
        add(bodyTable).grow().colspan(2)
    }



    inner class Tab(val title: String) : STable() {

        init {
            add(STextButton(title,tabButtonStyle))
        }

    }

}