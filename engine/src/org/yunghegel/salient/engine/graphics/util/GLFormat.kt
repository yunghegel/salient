package org.yunghegel.salient.engine.graphics.util

import com.badlogic.gdx.graphics.glutils.GLFrameBuffer.FrameBufferBuilder

data class GLFormat(val internalFormat: Int,val format: Int, val type: Int)

fun FrameBufferBuilder.attach(format: GLFormat) {
    addColorTextureAttachment(format.internalFormat, format.format, format.type)
}
