package org.yunghegel.salient.engine.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Separator
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.collections.toGdxArray

import org.yunghegel.gdx.utils.ext.drawable
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.gdx.utils.ext.padVertical
import org.yunghegel.gdx.utils.ext.rand.float
import org.yunghegel.gdx.utils.ui.ColorBox
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.*
import org.yunghegel.salient.engine.ui.table
import org.yunghegel.salient.engine.ui.widgets.value.widgets.LabeledFloatField
import org.yunghegel.salient.engine.ui.widgets.value.widgets.LabeledIntField
import org.yunghegel.salient.engine.ui.widgets.value.widgets.LabeledTextField

class InputTable(val title: String="") : STable() {

    val result = Result()

    var submit : (Result)->Unit = {}
    val content = table()

    init {
        pad(4f)
        add(content).grow().row()
    }

    var tmpT = STable()
    var tmp : MutableList<Actor> = mutableListOf()

    override fun row(): Cell<*> {
        return super.row()
    }

    var submitButton = STextButton("Submit")

    var addsubmit = true

    fun addSubmit() {
        addsubmit = true
    }

    fun addSubmit(with: (Result)->Unit) {
        addsubmit = true
        separator()
        row()
        submitButton.onChange {
            with(result)
        }
        content.add(submitButton)
    }

    fun InputTable.row(conf: InputTable.()->Unit) : Cell<*> {
        val table = InputTable()
        table.conf()
        val cell =add(table).padVertical(3f)
        cell.row()
        return cell
    }

    fun floatInput(name: String, default: Float = 0f, change: (Float)->Unit = {}) : Cell<LabeledFloatField> {
        val floatfield = LabeledFloatField(name,75)
        floatfield.float = default
        result[name] = default
        floatfield.changed {
            change(floatfield.float)
            result[name] = floatfield.float
            submit(result)
        }
        add(floatfield)
        return content.add(floatfield)
    }

    fun intInput(name: String, default: Int = 0, change: (Int)->Unit = {}) : Cell<LabeledIntField> {
        val intfield = LabeledIntField(name,75)
        intfield.int = default
        result[name] = default
        intfield.changed {
            change(intfield.int)
            result[name] = intfield.int
            submit(result)
        }
        add(intfield)
        return content.add(intfield)
    }

    fun button(name: String, action: () -> Unit) : Cell<STextButton> {
        val button = STextButton(name)
        button.onClick {
            action()
            submit(result)
        }
        add(button)
        return content.add(button)
    }

    fun label(text:String, style: String = "default") : Cell<SLabel> {
        val label = SLabel(text,style)
        return content.add(label)
    }

    fun image(drawable: String) : Cell<SImage> {
        val d = skin.drawable(drawable)
        val img = SImage(d)
        return content.add(img)
    }

    fun image(drawable: Drawable) : Cell<SImage> {
        val img = SImage(drawable)
        return content.add(img)
    }

    fun textInput(name: String, default: String, change: (String)->Unit = {}) : Cell<LabeledTextField> {
        val textfield = LabeledTextField(name)
        result[name] = default
        textfield.onChange {
            result[name] = textfield.text ?: default
            submit(result)
        }
       return content.add(textfield)
    }

    fun option(name: String, default: Boolean = false, text: String = "", change: (Boolean)->Unit= {}) : Cell<SCheckBox> {
        val button = SCheckBox("")
        result[name] = default
        button.onChange {
            result[name] = button.isChecked
            change(button.isChecked)
        }
        return content.add(button)
    }

    fun floatTupleInput(name: String, size: Int, default: Array<Float>, change: (Array<Float>)->Unit = {}) : Cell<STable> {
        val table = table {
            val result = default
            for (i in 0 until size) {
                float("$name$i", default[i], { res ->
                    default[i] = res
                    change(result)
                }).padHorizontal(5f)
            }
        }
        return content.add(table)
    }

    fun color(name: String, default: Color, change: (Color)->Unit = {}) : Cell<STable> {
        val colorfield = ColorBox(name, { default },false, UI.skin)
        colorfield.callback = {
            change(it)
            result[name] = it
            submit(result)
        }
        return content.add(table {
            add(colorfield).padHorizontal(5f)
        })
    }

    fun float(id: String, default: Float, change: (Float)->Unit = {}) : Cell<STable> {
        val floatfield = FloatField("", false, { default }, {
            change(it)
            result[id] = it
        })
        return content.add(floatfield)
    }

    fun choice(name:String, options: List<String>, default: String,change:(String)->Unit = {}) : Cell<STable> {
        val selectBox = SSelectBox<String>()
        selectBox.setItems(options.toGdxArray())
        selectBox.selected = default
        result[name] = default
        selectBox.onChange {
            change(selectBox.selected)
            result[name] = selectBox.selected
            submit(result)
        }
        val label = SLabel(name,"default-small")
        val table = table {
            add(label).padHorizontal(5f)
            add(selectBox).padHorizontal(5f)
        }
        result[name] = default
        return content.add(table)
    }

    fun <T> choice(name:String, options: List<String>, default: String,map: (String)->T, withType: (T)->Unit) : Cell<STable> {
        val selectBox = SSelectBox<String>()
        selectBox.setItems(options.toGdxArray())
        selectBox.selected = default
        result[name] = default
        selectBox.onChange {
            withType(map(selectBox.selected))
            result[name] = selectBox.selected
            submit(result)
        }
        val label = SLabel(name,"default-small")
        val table = table {
            add(label).padHorizontal(5f)
            add(selectBox).padHorizontal(5f)
        }
        result[name] = default
        return content.add(table)
    }

    fun separator() {
        add(Separator()).colspan(2).fillX().expandX().align(Align.center).row()
    }

    fun withResult(action: (Result) -> Unit) {
        submit = action
    }

    fun config(action: InputTable.() -> Unit) {
        action()



    }



}

