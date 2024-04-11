package org.yunghegel.gdx.renderer.mrt

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer.GLFrameBufferBuilder
import org.yunghegel.gdx.renderer.util.EnumMask
import org.yunghegel.gdx.renderer.util.forEach

enum class TextureAttachment(val layout : Int,val internalFormat : Int, val format : Int, val type : Int) {

    DIFFUSE(0, GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE),
    NORMAL(1, GL30.GL_RGB8, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE),
    POSITION(2, GL30.GL_RGB8, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE),
    DEPTH(3, GL30.GL_DEPTH_COMPONENT32F, GL30.GL_DEPTH_COMPONENT, GL30.GL_FLOAT),
    SPECULAR(2, GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE);

    val attachment = GL30.GL_COLOR_ATTACHMENT0 + layout

    context(GLFrameBuffer.FrameBufferBuilder)
    fun attach() {
        with(this@TextureAttachment) {
            when (this@TextureAttachment) {
                DEPTH-> addDepthTextureAttachment(internalFormat, type)
                else -> addColorTextureAttachment(internalFormat, format, type)
            }
        }
    }

    companion object {
        fun EnumMask<TextureAttachment>.toFbo(enumMask: EnumMask<TextureAttachment>) : FrameBuffer {
            val fbo = GLFrameBuffer.FrameBufferBuilder(
                Gdx.graphics.width ?: 1,
                Gdx.graphics.height ?: 1
            )
            with(fbo) {
                enumMask.forEach { attachment -> attachment.attach() }
            }

            return fbo.build()
        }
    }


}