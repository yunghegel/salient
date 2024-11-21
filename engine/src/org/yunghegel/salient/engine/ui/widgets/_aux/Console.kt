package org.yunghegel.salient.engine.ui.widgets.aux

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import ktx.collections.GdxArray
import ktx.collections.toGdxArray
import org.yunghegel.gdx.cli.CommandLine
import org.yunghegel.gdx.cli.util.*
import org.yunghegel.gdx.cli.util.StdOut.writeLn
import org.yunghegel.salient.engine.State
import org.yunghegel.salient.engine.api.EditorProjectManager
import org.yunghegel.salient.engine.events.lifecycle.onEditorInitialized
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.system.warn
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.widgets._aux.ConsoleEntries
import org.yunghegel.salient.engine.ui.widgets._aux.ConsoleEntry
import org.yunghegel.salient.engine.ui.widgets._aux.InputLine

val CLI = Console.Companion

class Console : STable() {

    val projMan: EditorProjectManager<*,*> = inject()

    var prompt: String = "[SKY]${projMan.currentProject?.name}[] : [GOLDENROD]${projMan.currentProject?.currentScene?.name}[] @ [ROYAL]${CLI.context.namespace}"

    val scroll: ScrollPane
    val entries = ConsoleEntries()
    val commandHistory = CommandHistory()

    val inputLine = InputLine(this) { input ->
        val _input = Ansi.replaceAnsi(input)
        submitInput(input.replace(prompt, ""))
    }




    var namespaces = GdxArray<String>().apply { addAll(context.namespaces.toGdxArray()) }




    init {
        scroll = ScrollPane(entries)
        scroll.setOverscroll(false, true)
        scroll.setSmoothScrolling(true)
        scroll.setScrollbarsVisible(true)
        add(scroll).grow().colspan(2).row()
        add(inputLine).growX()

        StdOut.writeLn += { writeLn(it) }
        StdOut.writeErr += { writeError(it) }

        CLI.context.nsChanged = { old, new ->
            prompt = "[SKY]${projMan.currentProject?.name}[] : [GOLDENROD]${projMan.currentProject?.currentScene?.name}[] @ [ROYAL][${new}]"
            inputLine.label.setText(prompt)
        }

        onEditorInitialized {
            with(context) {
                values.forEach { (namespace, command) ->
                    command.forEach { (name, cmd) ->
                    }
                }
                commands.forEach { (namespace, command) ->
                    StdOut.writeLn("| ${namespace.blue()} ->\n")
                    command.forEach { (name, cmd) ->
                        StdOut.writeLn("  ${name.cyan()}: ${cmd.function.parameters.map { it.name?.replace("kotlin.","")?.purple()}}\n")
                    }
                }
            }
        }

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
            inputLine.inputField.text = prompt

        }
    }

    fun writeLn(string: String) {
        entries.addEntry(ConsoleEntry("$string"))
        refresh()
    }

    fun writeError(string: String) {
        entries.addEntry(ConsoleEntry("[RED]$string[]"))
        refresh()
    }



    fun acceptAutocomplete(string:String) {

    }

    companion object : CommandLine() {
        var MAX_ENTRIES = 250
    }



}