package org.yunghegel.salient.engine.ui.widgets.aux

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import ktx.actors.onClick
import ktx.actors.onKey
import ktx.actors.setKeyboardFocus
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.STextButton
import org.yunghegel.salient.engine.ui.scene2d.STextField

class Console : STable() {

    val MAX_ENTRIES = 250
    val scroll: ScrollPane
    val entries = ConsoleEntries()
    val commandHistory = CommandHistory()

    val inputLine = InputLine { input ->
        entries.addEntry(ConsoleEntry(input))
        commandHistory.submitInput(input)

        refresh()
    }

    init {
        scroll = ScrollPane(entries)
        scroll.setOverscroll(false, true)
        scroll.setSmoothScrolling(true)
        scroll.setScrollbarsVisible(true)
        add(scroll).grow().row()
        add(inputLine).growX()
    }

    private fun refresh() {
        scroll.validate()
        scroll.scrollPercentY = 1f
        scroll.scrollPercentX = 0f
        scroll.setOverscroll(true, true)
    }



    fun acceptAutocomplete(string:String) {

    }

    class ConsoleEntry(input: String, success: Boolean = true) : STable() {
        init {
            add(SLabel(input).apply{ if (success) { setColor(0f,1f,0f,1f) } else { setColor(1f,0f,0f,1f) } }).growX().pad(4f)
        }
    }

    inner class ConsoleEntries : STable() {

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
            if (entries.size > MAX_ENTRIES) {
                entries.removeAt(0)
                removeActorAt(0, true)
            }
        }
    }

    inner class InputLine(val submit: (String)->Unit) : STable() {
        val inputField = STextField("","console")
        val sendButton = STextButton("Submit")

        init {
            pad(4f)
            add(inputField).growX().padRight(10f)
            add(sendButton)
            createListeners()
        }

        fun submit() {
            inputField.text = inputField.text.trim()
            if (inputField.text.isNotEmpty()) {
                submit(inputField.text)
                inputField.text = ""
                stage.keyboardFocus = null
            }
        }

        fun createListeners() {
            sendButton.onClick {
                submit()
            }
            onKey { key ->
                if (key.code == Input.Keys.ENTER ) submit()
                if (key.code == Input.Keys.TAB) acceptAutocomplete(inputField.text)
                commandHistory.keyListener(inputField.text, key.code)
            }
            val clickListener = object : ClickListener() {



                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                    if (stage.scrollFocus == null)
                    {
                        stage.setScrollFocus(scroll)
                        stage.keyboardFocus = inputField
                    }
                    super.enter(event, x, y, pointer, fromActor)
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                    if (stage.scrollFocus != null)
                    {
                        if (toActor !=null && toActor.isDescendantOf(this@Console))
                        {
                            stage.setScrollFocus(scroll)
                            stage.keyboardFocus = inputField

                        } else
                        {
                            inputField.setKeyboardFocus(false)
                            stage.setScrollFocus(null)
                            stage.keyboardFocus = null
                        }
                    }
                    super.exit(event, x, y, pointer, toActor)
                }

                override fun keyTyped(event: InputEvent?, character: Char): Boolean {
                    if (isOver) {
                        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) submit()
                    }
                    if (character.code == Input.Keys.ENTER ) submit()
                    if (character.code == Input.Keys.TAB) acceptAutocomplete(inputField.text)
                    commandHistory.keyListener(inputField.text, character.code)
                    return super.keyTyped(event, character)
                }


            }

            this@Console.addListener(clickListener)
        }
    }



}