package org.yunghegel.salient.engine.api.tool

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.badlogic.gdx.math.Vector2
import ktx.collections.GdxArray
import org.yunghegel.salient.engine.input.Input


abstract class MultipleInputTool(name:String, val maxPoints : Int = 1) : InputTool(name) {

    private var running = false

    protected val dots: GdxArray<Vector2> = GdxArray()

    protected abstract fun complete()

    protected fun abort() {}

    protected fun isRunning(): Boolean {
        return running
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        if (button == Input.Buttons.LEFT) {
            val out = Vector2()
            val worldPosition = snap(unproject(out,screenX.toFloat(), screenY.toFloat()))
            dots.add(worldPosition)
            onNewDot(worldPosition)
            running = true
            if (maxPoints >= 0 && dots.size >= maxPoints) {
                running = false
                complete()
                dots.clear()
            }
            return true
        } else if (maxPoints < 0 && button == Input.Buttons.RIGHT) {
            running = false
            complete()
            dots.clear()
            return true
        }
        return super.touchDown(screenX, screenY, pointer, button)
    }

    protected fun onNewDot(worldPosition: Vector2?) {}

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.ESCAPE) {
            running = false
            abort()
            return true
        }
        return super.keyDown(keycode)
    }

    override fun render(renderer: ShapeRenderer) {
        val s = pixelSize().scl(8f)
        renderer.begin(ShapeType.Filled)
        for (dot in dots) {
            renderer.rect(dot.x - s.x, dot.y - s.y, 2 * s.x, 2 * s.y)
        }
        renderer.end()
    }

}