package org.yunghegel.salient.engine.api.undo

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import org.yunghegel.salient.engine.system.debug

class ActionHistoryKeyListener(val history: ActionHistory) : InputAdapter() {

    val isCtrlPressed: Boolean
        get() = Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT)

    val isShiftPressed: Boolean
        get() = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)

    override fun keyDown(keycode: Int): Boolean {
        if (isCtrlPressed && keycode == Input.Keys.Z) {
            history.goBack()
            debug("attempting undo")
            return true
        }

        if (isCtrlPressed && isShiftPressed && keycode == Input.Keys.Z) {
            history.goForward()
            debug("attempting redo")
            return true
        }

        return false
    }

}