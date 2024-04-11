package org.yunghegel.gdx.renderer.util

import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool

class RenderablePool : Pool<Renderable>() {
    protected var obtained: Array<Renderable> = Array()

    override fun newObject(): Renderable {
        return Renderable()
    }

    override fun obtain(): Renderable {
        val renderable = super.obtain()
        renderable.environment = null
        renderable.material = null
        renderable.meshPart["", null, 0, 0] = 0
        renderable.shader = null
        obtained.add(renderable)
        return renderable
    }

    fun flush() {
        super.freeAll(obtained)
        obtained.clear()
    }
}