package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import com.ray3k.stripe.PopTableTooltipListener
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.scene2d.vis.visTooltip
import org.yunghegel.salient.engine.ui.scene2d.SCheckBox
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.widgets.InputTable

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

    onClick {
        val popListener = PopTableTooltipListener(Align.center,UI.skin)
        val table = popListener.popTable
        table.attachToActor(this)
        table.isHideOnUnfocus = true

        table.conf()
        table.layout()
        table.originX = table.width/2
        addListener(popListener)
        table.show(UI)
    }





}

fun table(conf: STable.(STable) -> Unit= {}) : STable {
    val table = STable()
    table.conf(table)
    return table
}

fun label(text: String) : SLabel {
    return SLabel(text)
}

fun label (text: ()->String) : SLabel {
    return SLabel(text()) { text() }
}

fun textbutton(text: String, action: ()->Unit) : SLabel {
    val label = SLabel(text)
    label.onClick {
        action()
    }
    return label
}

fun STable.child(conf: InputTable.(STable) -> Unit) : Cell<InputTable> {
    val table = InputTable()
    table.conf(table)
    return add(table)
}

class TableBuilder : STable() {

    fun text(text: String, setter: (()->String)? = null) : Cell<SLabel> {
        val label = if (setter !=null) label(setter) else label(text)
        return add(label)
    }

    fun button(text: String, action: ()->Unit) : Cell<SLabel> {
        val button = textbutton(text,action)
        return add(button)
    }

    fun boolean(label: String, value: Boolean, change: (Boolean)->Unit) : Cell<SCheckBox> {
        val checkbox = flag(label,value,change)
        return add(checkbox)
    }

}




