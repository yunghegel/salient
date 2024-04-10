package org.yunghegel.salient.engine.ui.widgets.value

import com.badlogic.gdx.scenes.scene2d.ui.TextField

class FloatOnlyFilter(private val allowNegative:Boolean = false) : TextField.TextFieldFilter {

    override fun acceptChar(textField: TextField?, c: Char): Boolean {
        return c.isDigit() || c == '.' || (allowNegative && c == '-')
    }

}