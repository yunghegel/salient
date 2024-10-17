package org.yunghegel.salient.engine.ui.widgets._aux

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ktx.actors.onClick
import ktx.actors.onKey
import ktx.actors.setKeyboardFocus
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.STextButton
import org.yunghegel.salient.engine.ui.scene2d.STextField
import org.yunghegel.salient.engine.ui.widgets.aux.Console

class InputLine(val console: Console, val submit: (String)->Unit) : STable() {


    val inputField = STextField("", "console")
    var namespace : String = "global"
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
            if (key.code == Input.Keys.ENTER) submit()
            if (key.code == Input.Keys.TAB) console.acceptAutocomplete(inputField.text)
            console.commandHistory.keyListener(inputField.text, key.code)
        }
        val clickListener = object : ClickListener() {



            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
                if (stage.scrollFocus == null)
                {
                    stage.setScrollFocus(console.scroll)
                    stage.keyboardFocus = inputField
                }
                super.enter(event, x, y, pointer, fromActor)
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int, toActor: Actor?) {
                if (stage.scrollFocus != null)
                {
                    if (toActor !=null && (toActor.isDescendantOf(console) || toActor == console) || console.children.any { it.isDescendantOf(toActor) })
                    {
                        stage.setScrollFocus(console.scroll)
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
                if (character.code == Input.Keys.ENTER) submit()
                if (character.code == Input.Keys.TAB) console.acceptAutocomplete(inputField.text)
                console.commandHistory.keyListener(inputField.text, character.code)
                return super.keyTyped(event, character)
            }


        }

        console.addListener(clickListener)
    }
}