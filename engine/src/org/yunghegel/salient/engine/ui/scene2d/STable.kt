package org.yunghegel.gdx.utils.ui.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import com.ray3k.stripe.PopTableClickListener
import ktx.actors.onClick
import org.yunghegel.salient.engine.ui.UI
open class STable : Table(UI.skin) {

    private val popups: MutableMap<()->Boolean, PopTable> = mutableMapOf()
    private val triggerAllHidden = false

    fun wrap(): STable {
        val table = STable()
        table.add(this)
        return table
    }

    fun childContainer(actor: Actor?): STable {
        val table = STable()
        if (actor != null) table.add(actor).grow()
        add(table).grow()
        return table
    }

    fun addPopupTable(conf: PopTableClickListener.() -> Unit = {},trigger: ()->Boolean) : PopTable {
        val listener = PopTableClickListener()
        listener.apply {
            conf()
        }
        addListener(listener)
        val table=  listener.popTable
        table.attachToActor(this, Align.center,Align.center)
//        onClick {
//            if (stage != null) {
//                table.show(stage)
//            } else table.hide()
//        }

        popups[trigger] = table
        return table
    }

    override fun act(delta: Float) {
        super.act(delta)
        popups.forEach { (trigger, table) ->
            if (table.isHidden) {
                if (trigger()) {
                    table.show(stage)
                }
            }
        }
    }



}
