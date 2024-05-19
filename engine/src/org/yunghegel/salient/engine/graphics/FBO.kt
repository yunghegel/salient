package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.GLTexture
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.FrameBufferMultisample
import com.badlogic.gdx.graphics.glutils.GLFormat
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer.FrameBufferBuilder
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import java.util.function.Consumer

object FBO {
    fun create(format: GLFormat, depth: Boolean): FrameBuffer {
        return create(format, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, depth)
    }

    fun createMultisample(format: GLFormat, depth: Boolean, samples: Int): FrameBufferMultisample {
        return createMultisample(format, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, depth, samples)
    }

    fun create(format: GLFormat, width: Int, height: Int, depth: Boolean): FrameBuffer {
        val b = FrameBufferBuilder(width, height)
        b.addColorTextureAttachment(format.internalFormat, format.format, format.type)
        if (depth) b.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT24)
        return b.build()
    }

    fun createMultisample(
        format: GLFormat,
        width: Int,
        height: Int,
        depth: Boolean,
        samples: Int
    ): FrameBufferMultisample {
        val b = FrameBufferBuilder(width, height)
        b.addColorTextureAttachment(format.internalFormat, format.format, format.type)
        if (depth) b.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT24)
        return FrameBufferMultisample(b, samples)
    }

    fun ensureScreenSize(fbo: FrameBuffer?, format: GLFormat): FrameBuffer? {
        return ensureSize(fbo, format, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight)
    }

    fun ensureScreenSize(fbo: FrameBuffer?, format: GLFormat, depth: Boolean): FrameBuffer? {
        return ensureSize(fbo, format, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, depth)
    }

    fun ensureScreenSize(
        fbo: FrameBufferMultisample?,
        format: GLFormat,
        depth: Boolean,
        samples: Int
    ): FrameBufferMultisample? {
        return ensureSize(fbo, format, Gdx.graphics.backBufferWidth, Gdx.graphics.backBufferHeight, depth, samples)
    }

    fun ensureSize(fbo: FrameBuffer?, format: GLFormat, match: FrameBuffer): FrameBuffer? {
        return ensureSize(fbo, format, match.width, match.height)
    }

    fun ensureSize(fbo: FrameBuffer?, format: GLFormat, texture: GLTexture): FrameBuffer? {
        return ensureSize(fbo, format, texture.width, texture.height)
    }

    @JvmOverloads
    fun ensureSize(
        fbo: FrameBuffer?,
        format: GLFormat,
        width: Int,
        height: Int,
        depth: Boolean = false,
        init: Consumer<FrameBuffer?>? = null
    ): FrameBuffer {
        var fbo = fbo
        if (fbo == null || fbo.width != width || fbo.height != height) {
            fbo?.dispose()
            fbo = create(format, width, height, depth)
            init?.accept(fbo)
        }
        return fbo
    }

    fun ensureSize(
        fbo: FrameBufferMultisample?,
        format: GLFormat,
        width: Int,
        height: Int,
        depth: Boolean,
        samples: Int
    ): FrameBufferMultisample {
        var fbo = fbo
        if (fbo == null || fbo.width != width || fbo.height != height || fbo.samples != samples) {
            fbo?.dispose()
            fbo = createMultisample(format, depth, samples)
        }
        return fbo
    }

    fun blit(batch: SpriteBatch, input: Texture?, output: FrameBuffer) {
        output.begin()
        blit(batch, input)
        output.end()
    }

    fun blit(batch: SpriteBatch, input: Texture?) {
        batch.projectionMatrix.setToOrtho2D(0f, 0f, 1f, 1f)
        batch.begin()
        batch.draw(input, 0f, 0f, 1f, 1f, 0f, 0f, 1f, 1f)
        batch.end()
    }

    fun blit(batch: SpriteBatch, input: Texture?, output: FrameBuffer, shader: ShaderProgram?) {
        batch.shader = shader
        blit(batch, input, output)
        batch.shader = null
    }

    fun blit(batch: SpriteBatch, input: Texture?, shader: ShaderProgram?) {
        batch.shader = shader
        blit(batch, input)
        batch.shader = null
    }

    fun blit(batch: SpriteBatch, input: Texture?, srcX: Float, srcY: Float, srcW: Float, srcH: Float) {
        batch.projectionMatrix.setToOrtho2D(0f, 0f, 1f, 1f)
        batch.begin()
        batch.draw(input, 0f, 0f, 1f, 1f, srcX, srcY, srcX + srcW, srcY + srcH)
        batch.end()
    }

    fun subBlit(batch: SpriteBatch, input: Texture?, dstX: Float, dstY: Float, dstW: Float, dstH: Float) {
        batch.projectionMatrix.setToOrtho2D(0f, 0f, 1f, 1f)
        batch.begin()
        batch.draw(input, dstX, dstY, dstW, dstH, 0f, 0f, 1f, 1f)
        batch.end()
    }
}
