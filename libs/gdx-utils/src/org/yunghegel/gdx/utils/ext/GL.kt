package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.BufferUtils
import ktx.app.clearScreen
import java.awt.Frame
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

private val buffer1i: IntBuffer = BufferUtils.newIntBuffer(1)
private val buffer16f: FloatBuffer = BufferUtils.newFloatBuffer(16)
private val buffer16i: IntBuffer = BufferUtils.newIntBuffer(16)
private val INT_BUFF = ByteBuffer
    .allocateDirect(64).order(ByteOrder.nativeOrder())
    .asIntBuffer()

private var complete = false

private var textureMaxSize = 0
private var textureMinSize = 0

@Synchronized
fun getBoundFboHandle(): Int {
    val intBuf: IntBuffer = INT_BUFF
    Gdx.gl.glGetIntegerv(GL20.GL_FRAMEBUFFER_BINDING, intBuf)
    return intBuf[0]
}

@Synchronized
fun getViewport(): IntArray {
    val intBuf: IntBuffer = INT_BUFF
    Gdx.gl.glGetIntegerv(GL20.GL_VIEWPORT, intBuf)

    return intArrayOf(
        intBuf[0], intBuf[1], intBuf[2],
        intBuf[3]
    )
}

private fun ensureComplete() {
    if (!complete) {
        textureMinSize = 4
        textureMaxSize = getInt(GL20.GL_MAX_TEXTURE_SIZE)
        complete = true
    }
}

/**
 * @return the maximum texture size allowed by the device.
 */
fun getTextureMaxSize(): Int {
   ensureComplete()
    return textureMaxSize
}

/**
 * @return the minimum texture size allowed by the device.
 */
fun getTextureMinSize(): Int {
    ensureComplete()
    return textureMinSize
}


fun getInt(pname: Int): Int {
    buffer16i.clear()
    Gdx.gl.glGetIntegerv(pname, buffer16i)
    return buffer16i.get(0)
}

fun getMaxSamples(): Int {
    return getInt(GL30.GL_MAX_SAMPLES)
}

fun getMaxSamplesPoT(): Int {
    val max = getInt(GL30.GL_MAX_SAMPLES)
    return if (max > 1) 32 - Integer.numberOfLeadingZeros(max - 1) else 0
}

fun clearScreen(color: Color) {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
    Gdx.gl.glClearColor(color.r, color.g, color.b, 1f)
}

//clear depth
fun clearDepth() {
    Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT)
}

//clear color
fun clearColor() {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
}

fun toOpenGLCoords(pixelXY: Vector2, widthHeight: Vector2): Vector2 {
    var x = pixelXY.x
    var y = pixelXY.y
    val w = widthHeight.x
    val h = widthHeight.y

    x = (x / w) * 2 - 1
    y = (y / h) * 2 - 1

    return Vector2(x, y)
}

fun toOpenGLCoords(pixelXY: Vector2): Vector2 {
    return toOpenGLCoords(pixelXY, Vector2(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()))
}

fun toOpenGLCoords(x: Float, y: Float): Vector2 {
    return toOpenGLCoords(Vector2(x, y))
}

fun toOpenGLCoords(x: Float, y: Float, w: Float, h: Float): Vector2 {
    return toOpenGLCoords(Vector2(x, y), Vector2(w, h))
}

fun toOpenGLCoords(x: Int, y: Int): Vector2 {
    return toOpenGLCoords(Vector2(x.toFloat(), y.toFloat()))
}

fun toOpenGLCoords(x: Int, y: Int, w: Int, h: Int): Vector2 {
    return toOpenGLCoords(Vector2(x.toFloat(), y.toFloat()), Vector2(w.toFloat(), h.toFloat()))
}

fun pass(fbo:FrameBuffer,x:Int=0,y:Int =0, width: Int = appwidth, height: Int = appheight, pass: (FrameBuffer)->Unit) : Texture{
    glEnable(GL30.GL_DEPTH_TEST)
    Gdx.gl.glBindRenderbuffer(GL30.GL_RENDERBUFFER, 0)
    fbo.begin()
    pass(fbo)
    fbo.end()
    val tex = fbo.colorBufferTexture
//    flip
    return tex
}



fun Texture.draw(batch: SpriteBatch,x:Int=0,y:Int =0, width: Int = appwidth, height: Int = appheight,conf:SpriteBatch.()->Unit = {}) {
    batch.conf()
    batch.begin()
    batch.draw(this,x.toFloat(),y.toFloat(),width.toFloat(),height.toFloat(),0f,0f,1f,1f)
    batch.end()
}

fun Texture.drawf(batch: SpriteBatch,x:Float=0f,y:Float =0f, width: Float = appwidth.toFloat(), height: Float = appheight.toFloat(),conf:SpriteBatch.()->Unit = {}) {
    batch.conf()
    batch.begin()
    batch.draw(this,x.toFloat(),y.toFloat(),width.toFloat(),height.toFloat(),0f,0f,1f,1f)
    batch.end()
}