package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer.FrameBufferBuilder
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport

class GenFrameBuffer : Disposable {
    var fbo: FrameBuffer? = null
    var viewport: Viewport? = null
    private val fboBaseColorTexture: Texture
        get () = fbo!!.colorBufferTexture
    var fboTexture: TextureRegion? = null
        private set
    var fboPixmap: Pixmap? = null
    var fbb: FrameBufferBuilder? = null
    var frameBuffer: FrameBuffer? = null



    constructor(hasDepth: Boolean) {
        val width = Gdx.graphics.width
        val height = Gdx.graphics.height

        run {
            val frameBufferBuilder = FrameBufferBuilder(
                Gdx.graphics.width,
                Gdx.graphics.height
            )
            frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE)
            // frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGB8, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE);
            // frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGB8, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE);
            frameBufferBuilder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT, GL30.GL_UNSIGNED_SHORT)
//            frameBufferBuilder.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT24)
            fbo = frameBufferBuilder.build()
        }
    }

    constructor(width: Int, height: Int, hasDepth: Boolean) {
        fbo = FrameBuffer(Pixmap.Format.RGBA8888, width, height, hasDepth)
    }

    fun begin(viewport: Viewport) {
        this.viewport = viewport
        //viewport.setScreenBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fbo!!.begin()
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        Gdx.gl.glViewport(
            viewport.screenX, viewport.screenY, viewport.screenWidth,
            viewport.screenHeight
        )
    }

    fun end() {
        fbo!!.end()
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)

        //apply smoothing
        fboBaseColorTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)

        fboTexture = TextureRegion(fboBaseColorTexture)
        fboTexture!!.flip(false, true)
    }

    override fun dispose() {
        fbo!!.dispose()
    }

    fun resize(viewport: ScreenViewport?) {
        //fbo.dispose();

        //fbo = new FrameBuffer(Pixmap.Format.RGBA8888, viewport.getScreenWidth(), viewport.getScreenHeight(), true);
    }

    fun ensureFboSize(width: Int, height: Int) {
        if (fbo!!.width != width || fbo!!.height != height) {
            fbo!!.dispose()
            fbo = FrameBuffer(Pixmap.Format.RGBA8888, width, height, true)
        }
    }
}
