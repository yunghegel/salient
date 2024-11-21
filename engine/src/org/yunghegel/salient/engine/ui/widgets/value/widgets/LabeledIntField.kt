package org.yunghegel.salient.engine.ui.widgets.value.widgets

import com.badlogic.gdx.scenes.scene2d.Touchable
import ktx.actors.onChange
import ktx.actors.onTouchEvent

class LabeledIntField(labelText: String, width: Int=50, allowNegative: Boolean=false) : LabeledTextField(labelText,width) {

    var lastValid = 0

    init {
        textField.textFieldFilter = IntOnlyFilter(allowNegative)
        touchable = Touchable.enabled
        onTouchEvent { event, x, y ->
            if (hit(x, y, false) == textField) {
                stage.setKeyboardFocus(textField)
            } else {
                stage.keyboardFocus = null
            }
        }
    }

    var int: Int
        get() {
            if (isValid()) {
                val valid = Integer.parseInt(textField.text)
                lastValid = valid
                return valid
            }
            return lastValid
        }
        set(value) {
            textField.text = value.toString()
        }

    fun isValid(): Boolean {
        return textField.text.matches(Regex("-?\\d*"))
    }

    fun changed(action: (Int) -> Unit) {
        textField.onChange {
            if (isValid()) {
                action(int)
            }
        }
    }
}