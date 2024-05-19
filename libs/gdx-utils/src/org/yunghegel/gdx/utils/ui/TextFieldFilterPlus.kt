package org.yunghegel.gdx.utils.ui

import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.kotcrab.vis.ui.widget.VisTextField
import com.kotcrab.vis.ui.widget.VisTextField.TextFieldFilter

class TextFieldFilterPlus(val filter: (String,Char)->Boolean) : TextFieldFilter, TextField.TextFieldFilter {

    override fun acceptChar(textField: VisTextField?, c: Char): Boolean {
        return filter(textField?.text ?: "", c)
    }

    override fun acceptChar(textField: TextField?, c: Char): Boolean {
        return filter(textField?.text ?: "", c)
    }

}

fun textFieldFilter(filter: (String,Char)->Boolean) = TextFieldFilterPlus(filter)

object Filters {
    val digitsOnly = textFieldFilter { _, c -> c.isDigit() }
    val floatOnly = textFieldFilter { text, c -> c.isDigit() || (c == '.' && !text.contains('.')) }
    val intOnly = textFieldFilter { text, c -> c.isDigit() || (c == '-' && !text.contains('-')) }
    val alphaOnly = textFieldFilter { _, c -> c.isLetter() }
    val alphaNumericOnly = textFieldFilter { _, c -> c.isLetterOrDigit() }
    val noFilter = textFieldFilter { _, _ -> true }
}