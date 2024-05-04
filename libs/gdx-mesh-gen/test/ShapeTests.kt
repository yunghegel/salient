package src

import com.badlogic.gdx.*
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.environment.PointLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import org.yunghegel.gdx.meshgen.builder.ModelCreator
import org.yunghegel.gdx.meshgen.builder.part.*
import shaders.GridRenderer
import java.util.*

object ShapeTests : Game() {

    val tests : Array<ShapeTest> = Array()

    override fun create() {
        Gdx.app.logLevel = Application.LOG_DEBUG
        setScreen(MultiShapeTest())
    }


}


fun main(args: kotlin.Array<String>) {
    val configuration = Lwjgl3ApplicationConfiguration()
    configuration.setWindowedMode(1200, 900)
    configuration.setResizable(true)
    configuration.useVsync(false)
    configuration.setForegroundFPS(0)
    configuration.setBackBufferConfig(8, 8, 8, 8, 8, 8, 16)
    configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32, 3, 2)
    Lwjgl3Application(ShapeTests, configuration)

}

class MultiShapeTest : ShapeTest() {

    val creators = Vector<ModelCreator>().apply {
        addAll(
            listOf(
                DiscCreator(),
                ArcCreator(),
                CircleCreator(),
                BoxCreator(),
                CapsuleCreator(),
                CubeCreator()

            )
        )
    }
    var index = 0

    override fun createShape(): ModelInstance {
        val creator = creators[index]
        index = (index + 1) % creators.size  // move to next creator, wrap around to 0 if end is reached
        return creator.instance()
    }

    override fun render(delta:Float) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            shape.model.dispose()
            shape = createShape()
        }
        super.render(delta)
    }

}

abstract class ShapeTest() : ScreenAdapter() {

    lateinit var shape: ModelInstance
    lateinit var batch: ModelBatch
    lateinit var camera: PerspectiveCamera
    lateinit var camController: CameraInputController
    lateinit var environment: Environment

    init {
        batch = ModelBatch()
        camera = PerspectiveCamera(80f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.near = 0.01f
        camera.far = 300f
        camera.position.set(1f,1.5f,1f)
        camera.lookAt(0f, 0f, 0f)
        camera.update()

        environment = Environment()
        environment.set(ColorAttribute(ColorAttribute.Fog, 0.13f, 0.13f, 0.13f, 1f))
        environment.set(ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, .1f))
        environment.add(DirectionalLight().set(0.8f, 0.2f, 0.2f, -1f, -2f, -0.5f))

        camController = CameraInputController(camera)

        camController.rotateAngle = 180f
        camController.translateButton = -1
        camController.forwardTarget = false
        camController.translateTarget=false
        camController.scrollTarget = false
        Gdx.input.inputProcessor = camController

        Gdx.gl.glCullFace(GL20.GL_NONE)



    }

    abstract fun createShape() : ModelInstance

    override fun show() {
        shape = createShape()
    }

    override fun render(delta: Float) {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        Gdx.gl.glClearColor(0.2f,0.2f,0.2f,1f)
        GridRenderer.render(camera)
        camera.update()
        batch.begin(camera)
        batch.render(shape, environment)
        shape.transform.rotate(Vector3.Y,delta)
        batch.end()
    }




}