package org.yunghegel.salient.engine.ui.widgets._aux

import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable

class ConsoleEntry(input: String, success: Boolean = true) : STable() {
    init {
        val label = SLabel(SLabel.replaceAnsiWithMarkup(input)).apply{ if (!success) { setColor(1f,0f,0f,1f) } }
        add(label).growX().row()

    }
}