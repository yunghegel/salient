package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Graphics
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.GL32
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import dev.lyze.gdxtinyvg.utils.WhitePixelUtils
import org.apache.commons.lang3.SystemUtils
import org.yunghegel.gdx.utils.ext.Platform
import org.yunghegel.salient.engine.helpers.Grid
import org.yunghegel.salient.engine.io.singleton
import org.yunghegel.salient.engine.io.Log.info
import space.earlygrey.shapedrawer.ShapeDrawer

object GFX : Graphics by Gdx.graphics, GL32 by Gdx.gl32 {

    lateinit var spriteBatch: SpriteBatch
    lateinit var debugDrawer: DebugDrawer
    lateinit var shapeDrawer: ShapeDrawer
    lateinit var grid : Grid
    lateinit var shapeRenderer: ShapeRenderer
    lateinit var whitePixel : TextureRegion

    fun buildSharedContext() {

        whitePixel = WhitePixelUtils.createWhitePixelTexture()
        spriteBatch = if (SystemUtils.IS_OS_MAC) Platform.createSpriteBatch() else SpriteBatch()
        debugDrawer = DebugDrawer()
        shapeDrawer = ShapeDrawer(spriteBatch)
        grid = Grid()
        shapeRenderer = if (SystemUtils.IS_OS_MAC) Platform.createShapeRenderer() else ShapeRenderer()

        singleton(spriteBatch)
        singleton(shapeDrawer)
        singleton(debugDrawer)
        singleton(grid)
        singleton(shapeRenderer)
        singleton(whitePixel)

        info("Shared graphics context built for injection ;")
    }



    val RGB8: GLFormat = GLFormat(GL30.GL_RGB8, GL20.GL_RGB, GL20.GL_UNSIGNED_BYTE)
    val RGB16: GLFormat = GLFormat(GL30.GL_RGB16F, GL20.GL_RGB, GL20.GL_FLOAT)
    val RGB32: GLFormat = GLFormat(GL30.GL_RGB32F, GL20.GL_RGB, GL20.GL_FLOAT)

    val RGBA8: GLFormat = GLFormat(GL30.GL_RGBA8, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE)
    val RGBA16: GLFormat = GLFormat(GL30.GL_RGBA16F, GL20.GL_RGBA, GL20.GL_FLOAT)
    val RGBA32: GLFormat = GLFormat(GL30.GL_RGBA32F, GL20.GL_RGBA, GL20.GL_FLOAT)

    val DEPTH16: GLFormat = GLFormat(GL30.GL_DEPTH_COMPONENT16, GL20.GL_DEPTH_COMPONENT, GL20.GL_UNSIGNED_SHORT)
    val DEPTH24: GLFormat = GLFormat(GL30.GL_DEPTH_COMPONENT24, GL20.GL_DEPTH_COMPONENT, GL20.GL_UNSIGNED_INT)
    val DEPTH32: GLFormat = GLFormat(GL30.GL_DEPTH_COMPONENT32F, GL20.GL_DEPTH_COMPONENT, GL20.GL_FLOAT)


}