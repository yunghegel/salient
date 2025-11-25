package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL30
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.PolygonBatch
import com.badlogic.gdx.graphics.g2d.PolygonSprite
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider
import com.badlogic.gdx.graphics.g3d.utils.ShapeCache
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.viewport.ScreenViewport
import dev.lyze.gdxtinyvg.utils.WhitePixelUtils
import org.yunghegel.salient.engine.system.InjectionContext
import org.yunghegel.gdx.utils.ext.notnull
import org.yunghegel.salient.engine.GraphicsModule
import org.yunghegel.salient.engine.graphics.debug.DebugContext
import org.yunghegel.salient.engine.graphics.shapes.utility.Grid
import org.yunghegel.salient.engine.graphics.util.DebugDrawer
import org.yunghegel.salient.engine.graphics.util.GLFormat
import org.yunghegel.salient.engine.helpers.BlinnPhongBatch
import org.yunghegel.salient.engine.helpers.DepthBatch
import org.yunghegel.salient.engine.helpers.PBRBatch
import org.yunghegel.salient.engine.helpers.WireBatch
import org.yunghegel.salient.engine.scene3d.SceneGraphicsResources
import org.yunghegel.salient.engine.system.InjectionContext.bindSingleton
import org.yunghegel.salient.engine.system.Log.info
import space.earlygrey.shapedrawer.ShapeDrawer

object GFX : GraphicsContext {

    override val spriteBatch: SpriteBatch = SpriteBatch()
    override val polygonBatch: PolygonBatch = PolygonSpriteBatch()
    override val font: BitmapFont = BitmapFont()
    override val debugDrawer: DebugDrawer = DebugDrawer()
    override val shapeRenderer: ShapeRenderer = ShapeRenderer()
    override val whitePixel : TextureRegion = WhitePixelUtils.createWhitePixelTexture()
    override val shapeDrawer: ShapeDrawer = ShapeDrawer(spriteBatch, whitePixel)
    override val grid : Grid = Grid()
    override val modelBatch: ModelBatch = ModelBatch()
    override val shapeCache: ShapeCache = ShapeCache()
    override val blinnPhongBatch: BlinnPhongBatch = BlinnPhongBatch()
    override val pbrBatch: PBRBatch = PBRBatch()
    override val depthBatch: DepthBatch = DepthBatch(DepthShaderProvider())
    override val wireBatch: WireBatch = WireBatch()
    override val perspectiveCamera: PerspectiveCamera = PerspectiveCamera(70f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    override val orthographicCamera: OrthographicCamera = OrthographicCamera().apply { setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()) }
    override val viewport: ScreenViewport = ScreenViewport(orthographicCamera)
    override val debugContext: DebugContext = DebugContext(shapeRenderer,perspectiveCamera,font,spriteBatch,modelBatch,shapeCache)
    override val environment: Environment = Environment()



    val bindvalues: InjectionContext.() -> Unit = {
        shapeRenderer.setAutoShapeType(true)

        bindSingleton(spriteBatch)
        bindSingleton(shapeDrawer)
        bindSingleton(debugDrawer)
        bindSingleton(grid)
        bindSingleton(shapeRenderer)
        bindSingleton(whitePixel)
        bind(GridConfig::class) { grid.config }
        bind(SharedGraphicsResources::class) { this }
        bind(SceneGraphicsResources::class) { this }
        bindSingleton(modelBatch)
        bindSingleton(blinnPhongBatch)
        bindSingleton(pbrBatch)
        bindSingleton(depthBatch)
        bindSingleton(wireBatch)
        bindSingleton(perspectiveCamera)
        bindSingleton(orthographicCamera)
        bindSingleton(viewport)
        bindSingleton(debugContext)
        bindSingleton(environment)
        bind<Camera> {
            perspectiveCamera
        }




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