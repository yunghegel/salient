package org.yunghegel.salient.engine.api.tool

import com.badlogic.gdx.math.Vector2



abstract class DrawTool (name: String) : InputTool(name) {

    private val current = Vector2()

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        unproject(current, screenX.toFloat(), screenY.toFloat())
        drawingStart(current)
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        unproject(current, screenX.toFloat(), screenY.toFloat())
        drawing(current)
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        unproject(current, screenX.toFloat(), screenY.toFloat())
        drawingEnd(current)
        return true
    }

    protected abstract fun drawingStart(position: Vector2?)
    protected abstract fun drawing(position: Vector2?)
    protected abstract fun drawingEnd(position: Vector2?)

}