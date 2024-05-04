package org.yunghegel.salient.engine.ui.layout

import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Separator

open class ScrollPanel : Panel() {

   private var pane: ScrollPane? = null

    init {

    }

    override fun build() {
        if(pane == null) pane = ScrollPane(bodyTable)
        clearChildren()
        align(Align.topLeft)
        titleTable.align(Align.left)
        addInternal(titleTable)!!.growX().left().height(if (titleTable.titleName ==null) 10f else 26f)
        row()
        addInternal(Separator())!!.growX().row()
        bodyTable.align(Align.topLeft)
        addInternal(pane!!)?.grow()?.row()
        bodyTable.touchable = Touchable.enabled
    }

}