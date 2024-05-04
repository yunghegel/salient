package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import com.ray3k.stripe.PopTableTooltipListener
import ktx.scene2d.vis.visTooltip
import org.yunghegel.salient.engine.ui.scene2d.SCheckBox
import org.yunghegel.salient.engine.ui.scene2d.STable

fun Actor.addTooltip(conf: STable.() -> Unit) {
    val table = STable()
    table.conf()
    visTooltip(table)
}

fun flag(label:String,value: Boolean, change: (Boolean) -> Unit) : SCheckBox {
    val checkbox = SCheckBox(label, value) {
        bool -> change(bool)
    }
    return checkbox
}

fun Actor.pop(conf: PopTable.() -> Unit) {
    val popListener = PopTableTooltipListener(Align.center,UI.skin)


    val table = popListener.popTable
    table.attachToActor(this)
    table.conf()
    table.layout()
    table.originX = table.width/2
    addListener(popListener)
}

fun table(conf: STable.() -> Unit= {}) : STable {
    val table = STable()
    table.conf()
    return table
}