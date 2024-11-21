package org.yunghegel.salient.engine.ui.widgets._aux

import org.yunghegel.gdx.cli.util.Ansi
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable

class ConsoleEntry(input: String, success: Boolean = true) : STable() {
    val label : SLabel
    init {

        label = SLabel(Ansi.replaceAnsi(input)).apply{ if (!success) { setColor(1f,0f,0f,1f) } }
        label.style.font.data.markupEnabled = true
        add(label).growX().row()

    }
}