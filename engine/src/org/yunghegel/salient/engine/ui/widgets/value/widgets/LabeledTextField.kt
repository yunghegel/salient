package org.yunghegel.salient.engine.ui.widgets.value.widgets

import ktx.actors.onChange
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.STextField

open class LabeledTextField (labelText: String, width: Int = -1) : STable() {
    private var width = -1

    protected var textField: STextField
    private val label: SLabel

    init {
        this.width = width
        textField = STextField(" ", "console")
        label = SLabel(labelText)
        setupUI()
    }

    private fun setupUI() {
        if (width > 0) {
            add(label).center().width(width * 0.2f).pad(0f, 2f, 0f, 2f).growX()
            add(textField).right().width(width * 0.8f).row()
        } else {
            add(label).center().expandX().pad(0f, 2f, 0f, 2f)
            add(textField).right().expandX().row()
        }
    }

    fun onChange(callback: () -> Unit) {
        textField.onChange { callback() }
    }

    var text: String?
        get() = textField.text
        set(text) {
            textField.setText(text)
        }

    fun setEditable(editable: Boolean) {
        textField.isDisabled = !editable
    }

    fun clearText() {
        textField.setText("")
    }

    fun setLabelText(text: String?) {
        label.setText(toString())
    }

    fun setTextFieldText(text: String?) {
        textField.setText(text)
    }
}