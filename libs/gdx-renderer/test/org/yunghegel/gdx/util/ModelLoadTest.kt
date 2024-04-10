package org.yunghegel.gdx.util

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController
import net.mgsx.gltf.loaders.gltf.GLTFLoader
import org.yunghegel.gdx.renderer.shader.WireframeShader

open class ModelLoadTest : ApplicationAdapter() {

    protected lateinit var model: Model
    protected lateinit var instance: ModelInstance

//    rendering

    protected lateinit var cam : PerspectiveCamera
    protected lateinit var camController : CameraInputController
    protected open lateinit var batch : ModelBatch
    protected lateinit var instances : List<ModelInstance>

    private lateinit var env : Environment

    override fun create() {
        model = ObjLoader().loadModel(Gdx.files.internal("models/obj/Suzanne.obj"))
        model.materials.forEach { mat ->
            mat.forEach { attr ->
                println(attr)
            }
        }

        instance = model.toInstance()
        cam = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        cam.position.set(10f, 10f, 10f)
        cam.lookAt(0f, 0f, 0f)
        cam.near = 1f
        cam.far = 300f
        cam.update()
        camController = CameraInputController(cam)
        Gdx.input.inputProcessor = camController
        batch = ModelBatch()
        instances = SampleModels.randomInstanceCount(4).transformEach{ mat4 ->
            mat4.randomInRadius(5f).rotateRandomly()
        }

        instances.forEach { instance ->
            val mat = instance.materials.first()
            SampleTextures.attachEach(mat, SampleTextures.DIFFUSE, SampleTextures.NORMAL, SampleTextures.SPECULAR)
        }

        env = Environment().apply {
            set(ColorAttribute.createDiffuse(Color.WHITE))
            set(ColorAttribute.createSpecular(Color.WHITE))
            add(DirectionalLight().set(Color.WHITE, -1f, -0.8f, -0.2f))
            set(ColorAttribute.createFog(Color(0.3f, 0.3f, 0.3f, 1f)))
        }
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

fun main () {
    ModelLoadTest().launch()
}