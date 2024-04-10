package org.yunghegel.salient.core.ui.fields

import com.badlogic.gdx.scenes.scene2d.ui.TextField

class FloatOnlyFilter : TextField.TextFieldFilter {

    override fun acceptChar(textField: TextField?, c: Char): Boolean {
        return c.isDigit() || c == '.' || c == '-'
    }

}