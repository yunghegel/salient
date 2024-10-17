package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import dev.lyze.gdxtinyvg.utils.WhitePixelUtils
import org.yunghegel.salient.engine.system.InjectionContext
import org.yunghegel.gdx.utils.ext.notnull
import org.yunghegel.salient.engine.GraphicsModule
import org.yunghegel.salient.engine.graphics.shapes.utility.Grid
import org.yunghegel.salient.engine.graphics.util.DebugDrawer
import org.yunghegel.salient.engine.graphics.util.GLFormat
import org.yunghegel.salient.engine.system.Log.info
import space.earlygrey.shapedrawer.ShapeDrawer

object GFX : GraphicsModule(), SharedGraphicsResources {

    override var spriteBatch: SpriteBatch by notnull()
    override var debugDrawer: DebugDrawer by notnull()
    override var shapeDrawer: ShapeDrawer by notnull()
    override var grid : Grid by notnull()
    override var shapeRenderer: ShapeRenderer  by notnull()
    override var whitePixel : TextureRegion by notnull()

    override val registry: InjectionContext.() -> Unit = {
        whitePixel = WhitePixelUtils.createWhitePixelTexture()
        spriteBatch = SpriteBatch()
        debugDrawer = DebugDrawer()
        shapeDrawer = ShapeDrawer(spriteBatch)
        grid = Grid()
        shapeRenderer = ShapeRenderer()
        shapeRenderer.setAutoShapeType(true)

        bindSingleton(spriteBatch)
        bindSingleton(shapeDrawer)
        bindSingleton(debugDrawer)
        bindSingleton(grid)
        bindSingleton(shapeRenderer)
        bindSingleton(whitePixel)
        bind(GridConfig::class) { grid.config }
        bind(SharedGraphicsResources::class) { this }

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