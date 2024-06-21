package org.yunghegel.salient.engine.ui.widgets._aux

import com.badlogic.gdx.utils.Align
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.widgets.aux.Console

class ConsoleEntries : STable() {

    val entries = mutableListOf<ConsoleEntry>()

    init {
        align(Align.topLeft)
        pad(4f)
    }

    fun addEntry(entry: ConsoleEntry) {
        validateEntry(entry)
        entries.add(entry)
        add(entry).growX().row()
    }

    fun validateEntry(entry: ConsoleEntry) {
        if (entries.size > Console.MAX_ENTRIES) {
            entries.removeAt(0)
            removeActorAt(0, true)
        }
    }
}