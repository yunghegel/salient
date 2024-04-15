package org.yunghegel.salient.editor.input

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor


abstract class AbstractInputSystem(priority: Int) : EntitySystem(priority), InputProcessor {

    constructor() : this(0)

    fun setProcessingInputs(processing: Boolean) {
        if (processing) {
            (Gdx.input.inputProcessor as InputMultiplexer).addProcessor(this)
        } else {
            (Gdx.input.inputProcessor as InputMultiplexer).removeProcessor(this)
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    fun scrolled(amount: Int): Boolean {
        return false
    }
}