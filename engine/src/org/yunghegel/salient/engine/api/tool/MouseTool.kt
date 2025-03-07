package org.yunghegel.salient.engine.api.tool

abstract class MouseTool(name: String, key: Int = -1) : InputTool(name, key) {

    var button = -1
    var key = -1

    protected val clickTracker = ClickTracker()

    protected var isDoubleClick: Boolean = false


    override fun keyDown(keycode: Int): Boolean {
        button = keycode
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        button = -1
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        this.button = button
        isDoubleClick = clickTracker.isDoubleClick()
        return false
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        this.button = -1
        return false
    }

    override fun touchCancelled(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun scrolled(amountX: Float, amountY: Float): Boolean {
        return false
    }

    protected class ClickTracker {
        private var lastClickTime: Long = 0
        private val doubleClickTime: Long = 200 // Time for double click in milliseconds

        fun isDoubleClick(): Boolean {
            val currentTime = System.currentTimeMillis()
            val isDoubleClick = currentTime - lastClickTime <= doubleClickTime
            lastClickTime = currentTime
            return isDoubleClick
        }
    }


}