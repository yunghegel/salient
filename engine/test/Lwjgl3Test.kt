import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.GL20
import ktx.app.clearScreen
import org.yunghegel.gdx.utils.ext.clearColor
import org.yunghegel.gdx.utils.ext.clearDepth
import org.yunghegel.gdx.utils.ext.glEnable

open class Lwjgl3Test : ApplicationAdapter(){

    val bundle : TestBundle by lazy { TestBundle() }

    var config = Lwjgl3ApplicationConfiguration().apply {setWindowedMode(800,600)}

    var drawAxes = true
    var drawRotateHelper = true
    var drawGrid = true

    init {
        config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32,3,2)
        config.setBackBufferConfig(8, 8, 8, 8, 24, 8, 4)
    }

    var execCreate : ()->Unit = {}

    var execRender : ()->Unit = {}

    fun use(block: TestBundle.()->Unit) {
        with(bundle) {
            block()
        }
    }

    override fun create() {
        glEnable(GL20.GL_DEPTH_TEST)
        glEnable(GL20.GL_CULL_FACE)
        execCreate()
    }

    override fun render() {
        clearScreen(0.1f,0.1f,0.1f,0f)
//        if(drawGrid) {
//            bundle.grid.render(bundle.cam)
//        }
        execRender()
//        if(drawAxes) {
//            clearDepth()
//            with(bundle) {
//                batch.begin(cam)
//                batch.render(axisArrows)
//                batch.render(rotateHelper)
//                batch.end()
//            }
//        }

    }

    fun run() {
         Lwjgl3Application(this, config)
    }

    operator fun invoke() {
        run()
    }

}

fun lwjgl3test(block : Lwjgl3Test.()->Unit) : Lwjgl3Test {
    val app = Lwjgl3Test()
    with(app) {
        block()
    }
    return app
}
