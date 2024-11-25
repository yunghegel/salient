package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import org.yunghegel.salient.engine.ui.scene2d.SImage
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable

open class PopupTable : STable() {

    private val title: STable = table()
    private val content: STable = table()

    init {
        super.add(title).growX().maxHeight(22f).row()
        super.add(content).grow().row()
        title.background = UI.drawable("tab_down", Color.WHITE)
        content.background = UI.drawable("panel_body_background")
    }

    fun icon(name: String) {
        val icon = SImage(name)
        title.prepend(icon).size(16f).pad(3f)
    }

    fun title(text: String) {
        title.add(SLabel(text)).growX().pad(3f)
    }

    override fun <A : Actor> add(actor: A): Cell<A> {
        return content.add(actor)
    }


}