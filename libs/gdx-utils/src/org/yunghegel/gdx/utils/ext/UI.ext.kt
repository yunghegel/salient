package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Tooltip
import com.ray3k.stripe.PopTable
import com.ray3k.stripe.PopTableClickListener
import ktx.actors.onChange
import ktx.actors.onClick
import org.yunghegel.gdx.utils.ui.widgets.ColorBox
import kotlin.math.log10

const val CENTER = 1
const val TOP = 2
const val BOTTOM = 4
const val LEFT = 8
const val RIGHT = 16
const val TOP_LEFT = 10
const val TOP_RIGHT = 18
const val BOTTOM_LEFT = 12
const val BOTTOM_RIGHT = 20

fun <T: Actor> T.addTip(conf: Table.() -> Unit) {
    val table = Table()
    table.conf()

    val tip = Tooltip.Builder(table).target(this).build()

}

fun Actor.getBounds(out:Rectangle) {
    out.set(x, y, width, height)
}

fun Cell<*>.padHorizontal(value: Float) : Cell<*> {
    padLeft(value)
    padRight(value)
    return this
}

fun Cell<*>.padVertical(value: Float) : Cell<*>{
    padTop(value)
    padBottom(value)
    return this
}

enum class ControlScale {
    LIN, LOG
}

const val DEFAULT_PADDING = 4f

fun Actor.newPopupTable(conf: PopTableClickListener.() -> Unit = {}): PopTable {
    val listener = PopTableClickListener()
    listener.apply {
        conf()
    }
    addListener(listener)
    val table = listener.popTable
    table.attachToActor(this, Align.topLeft, Align.bottomLeft)
    onClick {
        if (stage != null) {
            table.show(stage)
        } else table.hide()
    }
    return table
}

fun Slider.onChangeComplete(complete: (Float) -> Unit) {
    onChange {
        if(!isDragging)  {
            complete(value)
        }
    }
}

fun label(text:String, style: String = "default",skin:Skin, supplier:(() -> String)?): Label {
    val label : Label
    label = if (supplier != null) {
        object : Label(supplier(),skin) {
            override fun act(delta: Float) {
                setText(supplier())
                super.act(delta)
            }
        }
    } else {
        Label(text, skin)
    }
    return label
}

fun table(skin:Skin) :Table{
    return Table(skin)
}

fun slider(min: Float, max: Float, stepSize: Float, value: Float?,skin:Skin, change: (Float)->Unit={}, complete: (Float)->Unit={}): Slider {
    val slider = Slider(min, max, stepSize,false,skin)
    if (value != null) slider.setValue(value)
    slider.onChange {
        change(this.value)
    }
    slider.onChangeComplete {
        complete(slider.value)
    }
    return slider
}

fun slider(table:Table, skin:Skin = table.skin,width: Float = 100f, label: String = "", min: Float=0f, max: Float, step: Float, value: Float = max,row:Boolean = true, scale: ControlScale=ControlScale.LIN, callback: (Float) -> Unit): Slider {
    val stepSize = (max - min) / width

    val sMin = if (scale == ControlScale.LOG) log10(min.toDouble()).toFloat() else min
    val sMax = if (scale == ControlScale.LOG) log10(max.toDouble()).toFloat() else max
    val sStep = if (scale == ControlScale.LOG) log10(step.toDouble()).toFloat() else stepSize
    val sValue = if (scale == ControlScale.LOG) log10(value.toDouble()).toFloat() else value

    val num = Label(value.toString(),skin)
    val name = Label(label,skin)
    val slider = slider(sMin,sMax, sStep, sValue,skin) {
        val v = if (scale == ControlScale.LOG) Math.pow(10.0, it.toDouble()).toFloat() else it
        callback(v)
        num.setText(v.toString())
    }
    if (row) {
        table.row()
    }
    table.add(name).pad(DEFAULT_PADDING)
    table.add(slider).width(width).pad(DEFAULT_PADDING)
    table.add(num).pad(DEFAULT_PADDING)
    return slider
}

fun slideri(skin:Skin,min: Int, max: Int, stepSize: Int, value: Int?, change: (Int)->Unit={}, complete: (Int)->Unit={}): Slider {
    val slider = Slider(min.toFloat(), max.toFloat(), stepSize.toFloat(),false,skin)
    if (value != null) slider.setValue(value.toFloat())
    slider.onChange {
        change(this.value.toInt())
    }
    slider.onChangeComplete {
        complete(slider.value.toInt())
    }
    return slider
}

fun sliderTable(
    table: Table = Table(),
    skin: Skin = table.skin,
    width: Float = 100f,
    label: String,
    min: Float,
    max: Float,
    step: Float,
    value: Float,
    scale: ControlScale,
    callback: (Float) -> Unit
): Slider {
    val stepSize = (max - min) / width

    val sMin = if (scale == ControlScale.LOG) log10(min.toDouble()).toFloat() else min
    val sMax = if (scale == ControlScale.LOG) log10(max.toDouble()).toFloat() else max
    val sStep = if (scale == ControlScale.LOG) log10(step.toDouble()).toFloat() else stepSize
    val sValue = if (scale == ControlScale.LOG) log10(value.toDouble()).toFloat() else value


    val num = object : Label(value.toString(),skin) {
        override fun setText(newText: CharSequence?) {
            super.setText(trimFloat(newText.toString().toFloat(),2))
        }
    }
    val name = Label(label,skin)
    val slider = slider(sMin, sMax, sStep, sValue,skin) {
        val v = if (scale == ControlScale.LOG) Math.pow(10.0, it.toDouble()).toFloat() else it
        callback(v)
        num.setText(v.toString())
    }
    val container = Table()
    with(container) {
        add(name).pad(DEFAULT_PADDING)
        add(slider).width(width).pad(DEFAULT_PADDING)
        add(num).pad(DEFAULT_PADDING).width(25f)
    }
    table.add(container).pad(DEFAULT_PADDING).width(width).row()
    return slider
}

fun sliderTablei(
    table: Table = Table(),
    skin:Skin = table.skin,
    width: Float = 100f,
    label: String,
    min: Int,
    max: Int,
    step: Int,
    value: Int,
    scale: ControlScale,
    callback: (Int) -> Unit
): Table {
    val stepSize = (max - min) / width

    val sMin = if (scale == ControlScale.LOG) log10(min.toDouble()).toFloat() else min.toFloat()
    val sMax = if (scale == ControlScale.LOG) log10(max.toDouble()).toFloat() else max.toFloat()
    val sStep = if (scale == ControlScale.LOG) log10(step.toDouble()).toFloat() else stepSize.toFloat()
    val sValue = if (scale == ControlScale.LOG) log10(value.toDouble()).toFloat() else value.toFloat()

    val num =object : Label(value.toString(), skin) {
        override fun act(delta: Float) {
            setText(floatToString(value.toFloat(),2))
            super.act(delta)
        }
    }
    val name = Label(label,skin)
    val slider = slideri(skin,min, max, step, value) {
        val v = if (scale == ControlScale.LOG) Math.pow(10.0, it.toDouble()).toInt() else it
        callback(v)
        num.setText(v.toString())
    }
    val container = Table()
    container.add(name).pad(DEFAULT_PADDING)
    container.add(slider).pad(DEFAULT_PADDING)
    container.add(num).pad(DEFAULT_PADDING)
    table.add(container).pad(DEFAULT_PADDING).width(width).row()
    return table
}

fun toggle(table: Table, label: String, value: Boolean,row:Boolean = false, callback: (Boolean) -> Unit): CheckBox {
    val check = CheckBox(label,table.skin )
    check.isChecked = value
    check.onChange {
        callback(check.isChecked)
    }
    table.add(check).pad(DEFAULT_PADDING)
    if(row) {
        table.row()
    }
    return check
}
fun colorBox(table: Table, name: String, colorModel: Color, alpha: Boolean,skin:Skin=table.skin): Cell<*> {
    val t = Table()
    t.add(name)
    t.add(ColorBox(name, { colorModel }, alpha,skin))
    val cell: Cell<*> = table.add(t).left()
    table.row()
    return cell
}

fun colorBox(
    table: Table,
    name: String,
    colorModel: Color?,
    alpha: Boolean,
    skin:Skin = table.skin,
    row: Boolean
): Cell<*> {
    val t = Table()
    t.add(name)
    t.add(ColorBox(name, { colorModel!! }, alpha,skin ))
    if (row) {
        val cell: Cell<*> = table.add(t).left()
        table.row()
        return cell
    }
    return table.add(t).left()
}

fun dialog(content: Actor, title: String?,skin:Skin): Dialog {
    val dialog = Dialog(title, skin, "dialog")
    dialog.contentTable.add(content).row()
    // dialog.getContentVisTable().add(trig(skin, "Close", ()->dialog.hide()));
    dialog.button("Close", dialog)

    return dialog
}