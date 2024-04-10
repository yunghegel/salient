package org.yunghegel.gdx.util

import com.badlogic.gdx.Gdx
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

class TestKit() {

    constructor (apply:TestKit.()->Unit) : this() {
        apply()
    }

    var model: Model
    var instance: ModelInstance
    var instances : Array<ModelInstance>
//    rendering
    var cam : PerspectiveCamera
    var camController : CameraInputController
    var batch : ModelBatch

    var env : Environment

    init {
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

        batch = ModelBatch()
        env = Environment().apply {
            set(ColorAttribute.createDiffuse(Color.WHITE))
            set(ColorAttribute.createSpecular(Color.WHITE))
            add(DirectionalLight().set(Color.WHITE, -1f, -0.8f, -0.2f))
            set(ColorAttribute.createFog(Color(0.3f, 0.3f, 0.3f, 1f)))
        }
        cam.update()
    }

    fun render(instead:Boolean = false,also: TestKit.()->Unit = {}) {
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT or GL20.GL_COLOR_BUFFER_BIT)
        if(!instead) {
            batch.begin(cam)
            batch.render(instance, env)
            batch.end()
        }
        also()

    }


}