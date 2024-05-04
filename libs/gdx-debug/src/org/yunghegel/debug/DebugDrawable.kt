package org.yunghegel.debug

import org.yunghegel.gdx.utils.data.Mask

interface DebugDrawable : Mask {

    val shouldDraw : Boolean
        get() = has(RENDER)

    fun renderDebug(debugContext: DebugContext)

}