package org.yunghegel.salient.engine

import com.badlogic.gdx.graphics.g3d.Renderable
import org.yunghegel.salient.engine.helpers.Pools

interface ObjectRenderable {

    val renderable: Renderable

    val get: Renderable
        get() = Pools.renderablePool.obtain()!!

    fun buildRenderable(renderable: Renderable) : Renderable



}