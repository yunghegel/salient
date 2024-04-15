package org.yunghegel.salient.engine.tool

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Vector2
import org.yunghegel.salient.engine.input.Input
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


abstract class RectangleTool(name:String) : InputTool(name) {

    protected var startPoint: Vector2 = Vector2()
    protected var endPoint: Vector2 = Vector2()
    protected var buttonFilter: Int = Input.Buttons.LEFT

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == buttonFilter) {
            startPoint = snap(unproject(out,screenX.toFloat(), screenY.toFloat()))
            begin(startPoint)
            return true
        }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        if (Gdx.input.isButtonPressed(buttonFilter)) {
            endPoint = snap(unproject(out,screenX.toFloat(), screenY.toFloat()))
        }
        return super.touchDragged(screenX, screenY, pointer)
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == buttonFilter) {
            create(startPoint, endPoint)
        }
        endPoint = Vector2()
        startPoint = endPoint
        return super.touchUp(screenX, screenY, pointer, button)
    }

    protected abstract fun create(startPoint: Vector2, endPoint: Vector2)

    protected fun begin(startPoint: Vector2) {}

    override fun render(renderer: ShapeRenderer) {
        val x = min(startPoint.x.toDouble(), endPoint.x.toDouble()).toFloat()
        val y = min(startPoint.y.toDouble(), endPoint.y.toDouble()).toFloat()
        val w = max(0.0, abs((startPoint.x - endPoint.x).toDouble())).toFloat()
        val h = max(0.0, abs((startPoint.y - endPoint.y).toDouble())).toFloat()
        renderer.begin(ShapeType.Line)
        renderer.rect(x, y, w, h)
        renderer.end()
    }
}