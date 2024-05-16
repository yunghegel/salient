package org.yunghegel.salient.engine.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Separator
import ktx.actors.onChange
import ktx.actors.onClick
import ktx.collections.toGdxArray
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.engine.ui.scene2d.*
import org.yunghegel.salient.engine.ui.table
import org.yunghegel.salient.engine.ui.widgets.value.widgets.LabeledFloatField
import org.yunghegel.salient.engine.ui.widgets.value.widgets.LabeledTextField

class InputTable(val title: String) : STable() {

    val result = Result()

    var submit : (Result)->Unit = {}
    val content = table()

    init {
       pad(15f)
        add(content).grow().row()

    }

    fun addSubmit() {
        add(STextButton("Submit"){
            submit(result)
        }).right().pad(5f)
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
        return content.add(floatfield)
    }

    fun button(name: String, action: () -> Unit) : Cell<STextButton> {
        val button = STextButton(name)
        button.onClick {
            action()
            submit(result)
        }
        return content.add(button)
    }

    fun textInput(name: String, default: String) : Cell<LabeledTextField> {
        val textfield = LabeledTextField(name)
        result[name] = default
        textfield.onChange {
            result[name] = textfield.text ?: default
            submit(result)
        }
       return content.add(textfield)
    }

    fun option(name: String, default: Boolean = false) : Cell<SCheckBox> {
        val button = SCheckBox(if(default) "True" else "False")
        result[name] = default
        button.onClick {
            result[name] = !default
            button.setText(if(result[name] as Boolean) "True" else "False")
            submit(result)
        }
        return content.add(button)
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
        add(Separator()).colspan(2).fillX().expandX().align(Align.center)
    }

    fun withResult(action: (Result) -> Unit) {
        submit = action
    }

    fun config(action: InputTable.() -> Unit) {
        action()
        row()
        add(table{STextButton("Submit"){
            submit(result)
        } }).right()

    }



}

