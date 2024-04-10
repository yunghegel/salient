package org.yunghegel.gdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector3
import org.yunghegel.gdx.renderer.Renderer
import org.yunghegel.gdx.util.*

class ObjRenderingTest : App() {

    lateinit var testKit: TestKit
    lateinit var renderer: Renderer


    override fun create() {
        testKit = TestKit {
            model = SampleModels.TORUS_KNOT.load("models","obj")
            instance = model.toInstance()
            cam.position.set(2.5f,3f,3f)
            cam.lookAt(0f,0f,0f)
            println(model.meshes.first().vertexAttributes)
            cam.update()
        }
        renderer = Renderer()
    }

    override fun render() {
        super.render()
        testKit.render {

        }
        renderer.render(listOf(testKit.instance))
    }



}

fun main() {
    ObjRenderingTest().run()
}
