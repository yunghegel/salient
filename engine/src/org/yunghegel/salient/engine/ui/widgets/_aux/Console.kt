package org.yunghegel.salient.engine.ui.widgets.aux

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import org.yunghegel.gdx.cli.CommandLine
import org.yunghegel.gdx.cli.StdOut
import org.yunghegel.salient.engine.system.warn
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.widgets._aux.ConsoleEntries
import org.yunghegel.salient.engine.ui.widgets._aux.ConsoleEntry
import org.yunghegel.salient.engine.ui.widgets._aux.InputLine

class Console : STable() {

    val scroll: ScrollPane
    val entries = ConsoleEntries()
    val commandHistory = CommandHistory()

    val inputLine = InputLine(this) { input ->
        submitInput(input)
    }



    init {
        scroll = ScrollPane(entries)
        scroll.setOverscroll(false, true)
        scroll.setSmoothScrolling(true)
        scroll.setScrollbarsVisible(true)
        add(scroll).grow().row()
        add(inputLine).growX()

        StdOut.writeLn = { writeLn(it) }

    }

    private fun refresh() {
        scroll.validate()
        scroll.scrollPercentY = 1f
        scroll.scrollPercentX = 0f
        scroll.setOverscroll(true, true)
    }

    fun submitInput(input: String) {
        writeLn(input)
        try {
            acceptInput(input)
        } catch (e: Exception) {
            warn("Error: ${e.message}")
        } finally {
            inputLine.inputField.text = ""

        }
    }

    fun writeLn(string: String) {
        entries.addEntry(ConsoleEntry(string))
        refresh()
    }



    fun acceptAutocomplete(string:String) {

    }

    companion object : CommandLine() {
        var MAX_ENTRIES = 250
    }



}