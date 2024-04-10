package org.yunghegel.debug

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import org.yunghegel.gdx.utils.data.Mask

interface DebugDrawable : Mask {

    val shouldDebugDraw : Boolean
        get() = has(ENABLED)

    fun renderDebug(debugContext: DebugContext)

}