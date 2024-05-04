import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.GLFrameBuffer.FrameBufferBuilder
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.yunghegel.gdx.utils.ext.appheight
import org.yunghegel.gdx.utils.ext.appwidth
import org.yunghegel.gdx.utils.ext.instance
import org.yunghegel.salient.engine.graphics.shapes.utility.AxisArrows
import org.yunghegel.salient.engine.graphics.shapes.utility.Grid
import org.yunghegel.salient.engine.graphics.shapes.utility.RotationGizmoShape
import org.yunghegel.salient.engine.helpers.SampleModels
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.STable

class TestBundle() {

    constructor (apply:TestBundle.()->Unit) : this() {
        apply()
    }

    lateinit var model: Model
    lateinit var instance: ModelInstance
    lateinit var instances : Array<ModelInstance>
    lateinit var cam : PerspectiveCamera
    lateinit var camController : CameraInputController
    lateinit var batch : ModelBatch
    lateinit var depth : ModelBatch
    lateinit var env : Environment
    lateinit var viewport : ScreenViewport
    lateinit var grid : Grid

    lateinit var axisArrows : ModelInstance
    lateinit var rotateHelper : ModelInstance

    lateinit var stage : Stage
    lateinit var root : STable
    lateinit var spriteBatch: SpriteBatch
    lateinit var font : BitmapFont
    lateinit var shapeRenderer : ShapeRenderer

    val fbo0 : FrameBuffer by lazy {
        val builder = FrameBufferBuilder(appwidth, appheight)
        builder.addColorTextureAttachment(GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE)
        // frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGB8, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE);
        // frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGB8, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE);
        builder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT, GL30.GL_UNSIGNED_SHORT)
        builder.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT24)
        builder.build()
    }
    val fbo1 : FrameBuffer by lazy {
        val builder = FrameBufferBuilder(appwidth, appheight)
        builder.addBasicColorTextureAttachment(Pixmap.Format.RGBA8888)
        builder.addDepthTextureAttachment(GL20.GL_DEPTH_COMPONENT, GL20.GL_FLOAT)
        builder.addDepthRenderBuffer(GL30.GL_DEPTH_COMPONENT24)
        builder.build()    }

    fun init3D(attachHelpers: Boolean = true, modelenum :SampleModels? = null) {
        instances = Array()

        model = modelenum?.load() ?: SampleModels.random()
        instance = model.instance
        if (attachHelpers){
            axisArrows = AxisArrows().attach(instance.transform)
            rotateHelper = RotationGizmoShape().attach(model,instance.transform)
        } else {
            rotateHelper = RotationGizmoShape().buildModel().instance
            axisArrows = AxisArrows().buildModel().instance
        }
        instances.add(instance)
        grid = Grid()
        cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())


//        viewport = ScreenViewport(cam)
//        viewport.apply()

        cam.position.set(2f, 2f, 2f)
        cam.lookAt(0f, 0f, 0f)
        cam.near = 0.5f
        cam.far = 50f
        cam.update()

        camController = CameraInputController(cam)
        Gdx.input.inputProcessor = camController

        batch = ModelBatch()

        depth = ModelBatch(DepthShaderProvider())

        env = Environment().apply {
            set(ColorAttribute.createDiffuse(Color.WHITE))
            set(ColorAttribute.createSpecular(Color.WHITE))
            add(DirectionalLight().set(Color.WHITE, -1f, -0.8f, -0.2f))
            set(ColorAttribute.createFog(Color(0.3f, 0.3f, 0.3f, 1f)))
        }
        cam.update()

        shapeRenderer = ShapeRenderer()
        shapeRenderer.setAutoShapeType(true)
    }

    fun init2D() {
        UI.init()
        stage = Stage()
        root = STable()
        spriteBatch = SpriteBatch()
        font = UI.font
    }

    fun render(instead:Boolean = false,also: TestBundle.()->Unit = {}) {
        if(!instead) {
            draw3D()
            draw2D()
        }
        also()
    }

    fun draw3D() {
        batch.begin(cam)
        batch.render(instances, env)
        batch.end()
    }

    fun draw2D() {
        stage.act()
        stage.draw()
    }

    fun renderText(text: String, x: Float, y: Float) {
        spriteBatch.begin()
        font.draw(spriteBatch, text, x, y)
        spriteBatch.end()
    }

}