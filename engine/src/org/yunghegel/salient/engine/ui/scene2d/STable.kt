package org.yunghegel.salient.engine.ui.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Table.Debug.actor
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import com.ray3k.stripe.PopTableClickListener
import ktx.actors.onClick
import org.yunghegel.gdx.utils.ext.dimensions
import org.yunghegel.salient.engine.ui.UI
open class STable : Table(UI.skin) {

    private val popups: MutableMap<()->Boolean, PopTable> = mutableMapOf()
    private val triggerAllHidden = false

    var widthFunction : (() -> Float)? = null
    var heightFunction : (() -> Float)? = null

    override fun getPrefWidth(): Float {
        if (widthFunction!=null) return widthFunction!!.invoke()
        return super.getPrefWidth()
    }

    override fun getPrefHeight(): Float {
        if (heightFunction!=null) return heightFunction!!.invoke()
        return super.getPrefHeight()
    }

    fun <T : Actor> prepend(actor: T): Cell<T> {
        val cell = add(actor)
        if (children.size > 1) {
            val index = children.indexOf(actor)
            children.swap(index, 0)
        }
        return cell
    }

    fun constrainAsPercentOfActor(actor:Actor,percentW: Float, percentH: Float, minimum: Float, maximum:Float) {
        val (w,h) = actor.dimensions
        widthFunction = { Math.min(Math.max(w*percentW,minimum),maximum) }
        heightFunction = { Math.min(Math.max(h*percentH,minimum),maximum) }
    }

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
