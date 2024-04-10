package org.yunghegel.salient.editor.render

import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer.FrameBufferBuilder
import org.lwjgl.opengl.GL30
import org.yunghegel.salient.editor.tool.Tool
import org.yunghegel.salient.engine.graphics.GFX
import org.yunghegel.salient.engine.graphics.attach
import org.yunghegel.salient.engine.io.inject

class FBOTool : Tool("fbo_tool") {

    val cache : MutableMap<String,FrameBuffer> = mutableMapOf()

    val batch : SpriteBatch = inject()


    private var builder : FrameBufferBuilder? = null

    fun getTexture(name:String) : Texture? {
        return cache[name]?.colorBufferTexture
    }

    private fun startbuild(width:Int, height:Int) {
        builder = FrameBufferBuilder(width, height)
    }

    fun addColor(internalFormat: Int,format:Int,type:Int) {
        builder?.addColorTextureAttachment(internalFormat, format,type)
    }

    fun addDepth(internalFormat: Int) {
        builder?.addDepthRenderBuffer(internalFormat)
    }

    fun addStencil(internalFormat: Int) {
        builder?.addStencilRenderBuffer(internalFormat)
    }

    private fun finish(name:String) : FrameBuffer {
        cache[name] = builder?.build()!!
        return cache[name]!!
    }

    fun buildnew(name:String, width:Int, height:Int,format: Pixmap.Format = Pixmap.Format.RGBA8888) : FBOTool {
        startbuild(width, height)
        return this
    }

    fun makeDefault(name:String, depth:Boolean) : FrameBuffer {
        startbuild(1024, 1024)
        builder?.attach(GFX.RGBA32)
        if (depth) builder?.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT32F)
        return finish(name)
    }

    fun capture(name:String, draw:()->Unit) : Texture {
        val fbo = cache[name]!!
        fbo.begin()
        draw()
        fbo.end()
        return fbo.colorBufferTexture
    }

    fun render(name:String) {
        batch.begin()
        batch.draw(getTexture(name),0f,0f)
        batch.end()
    }



}