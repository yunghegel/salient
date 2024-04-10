package org.yunghegel.gdx

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.g3d.ModelBatch
import org.yunghegel.gdx.renderer.shader.WireframeShader
import org.yunghegel.gdx.util.*

class WireframeShaderTest : App() {
    lateinit var testkit : TestKit

    lateinit var batch2 : ModelBatch

    override fun create() {
        batch2 = ModelBatch()
       testkit = TestKit {
            batch = ModelBatch(WireframeShader)
            model = SampleModels.TORUS_KNOT.load("models", "obj")
            instance = model.toInstance()
            cam.position.set(2.5f, 3f, 3f)
            cam.lookAt(0f, 0f, 0f)
            println(model.meshes.first().vertexAttributes)
            cam.update()
        }
    }

    override fun render() {
        super.render()




        testkit.render {
            batch2.begin(cam)
            batch2.render(instance)
            batch2.end()
            batch.begin(cam)
            batch.render(instance)
            batch.end()

        }
    }

}

fun main() {
    WireframeShaderTest().launch { conf ->
        conf.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32, 3, 2)
        conf.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4)

    }
}