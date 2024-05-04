package org.yunghegel.gdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import com.badlogic.gdx.utils.Array
import org.yunghegel.gdx.renderer.shader.BlinnPhongShader
import org.yunghegel.gdx.util.App
import org.yunghegel.gdx.util.SampleModels
import org.yunghegel.gdx.util.toInstance

class DefaultShaderTest : App() {

    lateinit var model: Model
    lateinit var instance: ModelInstance
    lateinit var instances : Array<ModelInstance>
    //    rendering
    lateinit var cam : PerspectiveCamera
    lateinit var camController : CameraInputController
    lateinit var batch : ModelBatch
    lateinit var env : Environment

    val directionalLight = org.yunghegel.gdx.renderer.env.DirectionalLight()

    override fun create() {

        model = SampleModels.random()
        instance = model.toInstance()
        instances = Array()
        println(model.meshes.first().vertexAttributes)

        cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.position.set(2f, 2f, 2f)
        cam.lookAt(0f, 0f, 0f)
        cam.near = 1f
        cam.far = 300f
        cam.update()
        camController = CameraInputController(cam)
        Gdx.input.inputProcessor = camController

        batch = ModelBatch(BlinnPhongShader)
        env = Environment().apply {
            set(ColorAttribute.createDiffuse(Color.WHITE))
            set(ColorAttribute.createSpecular(Color.WHITE))
            add(DirectionalLight().set(Color.WHITE, -1f, -0.8f, -0.2f))
            set(ColorAttribute.createFog(Color(0.3f, 0.3f, 0.3f, 1f)))
        }
        cam.update()
    }

    override fun render() {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        camController.update()
        batch.begin(cam)
        batch.render(instance,env)
        batch.end()
    }

}

fun main() {
    DefaultShaderTest().run {
        setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32,3,2)
    }
}