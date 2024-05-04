package org.yunghegel.gdx.utils.selection

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.HdpiMode
import com.badlogic.gdx.graphics.glutils.HdpiUtils
import com.badlogic.gdx.utils.BufferUtils
import com.badlogic.gdx.utils.viewport.Viewport
import java.util.logging.Logger

class Picker {
    var fbo: FrameBuffer? = null

    init {
        var width = Gdx.graphics.width
        var height = Gdx.graphics.height

        try {
            fbo = FrameBuffer(Pixmap.Format.RGBA8888, width, height, true)
        } catch (e: Exception) {
            width = (width * 0.9f).toInt()
            height = (height * 0.9f).toInt()
            try {
                fbo = FrameBuffer(Pixmap.Format.RGBA8888, width, height, true)
            } catch (ee: Exception) {
                Logger.getGlobal().warning("Failed to create FrameBuffer for ToolHandlePicker")
            }
        }
    }

    /**
     * Call when you want to pick an object.
     * You should call update on your [Viewport] before this method and your draw calls after updating the viewport.
     * @param viewport
     * @param batch
     * @param cam
     * @param screenX
     * @param screenY
     * @param pickables
     * @return
     */
    fun pick(
        viewport: Viewport,
        batch: ModelBatch,
        cam: Camera,
        screenX: Int,
        screenY: Int,
        pickables: List<Pickable>
    ): Pickable? {
//        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        begin(viewport)
        renderPickables(batch, cam, pickables)
        end()
        val pm = getFrameBufferPixmap(viewport)

        val x = screenX - viewport.screenX
        val y = screenY - (Gdx.graphics.height - (viewport.screenY + viewport.screenHeight))
        val id = PickerColorEncoder.decode(pm.getPixel(x, y))
        for (pickable in pickables) {
            if (pickable.id.equals(id)) {
                return pickable
            }
        }

        return null
    }

    private fun renderPickables(batch: ModelBatch, cam: Camera, pickables: List<Pickable>) {
        batch.begin(cam)
        for (pickable in pickables) {
            pickable.renderPick(batch)
        }
        batch.end()
    }

    val texture: Texture?
        get() {
            if (fbo == null) return null
            return fbo!!.colorBufferTexture
        }

    protected fun begin(viewport: Viewport) {
        // Per LibGDX WIKI, we need to set to Pixels temporarily when capturing Scene2D elements otherwise we run into
        // issues on retina screens. Observed issues on Mac with tools not detecting clicks
        // https://libgdx.com/wiki/graphics/opengl-utils/frame-buffer-objects
        HdpiUtils.setMode(HdpiMode.Pixels)
        fbo!!.begin()
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        HdpiUtils.glViewport(
            viewport.screenX, viewport.screenY, viewport.screenWidth,
            viewport.screenHeight
        )
    }

    protected fun end() {
        fbo!!.end()
        HdpiUtils.setMode(HdpiMode.Logical)
    }

    fun getFrameBufferPixmap(viewport: Viewport): Pixmap {
        val w = viewport.screenWidth
        val h = viewport.screenHeight
        val x = viewport.screenX
        val y = viewport.screenY
        val pixelBuffer = BufferUtils.newByteBuffer(w * h * 4)

        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, fbo!!.framebufferHandle)
        Gdx.gl.glReadPixels(x, y, w, h, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, pixelBuffer)
        Gdx.gl.glBindFramebuffer(GL20.GL_FRAMEBUFFER, 0)

        val numBytes = w * h * 4
        val imgLines = ByteArray(numBytes)
        val numBytesPerLine = w * 4
        for (i in 0 until h) {
            pixelBuffer.position((h - i - 1) * numBytesPerLine)
            pixelBuffer[imgLines, i * numBytesPerLine, numBytesPerLine]
        }

        val pixmap = Pixmap(w, h, Pixmap.Format.RGBA8888)
        BufferUtils.copy(imgLines, 0, pixmap.pixels, imgLines.size)

        return pixmap
    }
}
