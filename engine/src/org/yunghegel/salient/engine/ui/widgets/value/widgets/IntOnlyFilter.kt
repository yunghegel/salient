package org.yunghegel.salient.engine.ui.widgets.value.widgets

import com.badlogic.gdx.scenes.scene2d.ui.TextField

class IntOnlyFilter(val allowNegative: Boolean) :  TextField.TextFieldFilter  {
    override fun acceptChar(textField: TextField?, c: Char): Boolean {
        return c.isDigit() || (allowNegative && c == '-')
    }
}