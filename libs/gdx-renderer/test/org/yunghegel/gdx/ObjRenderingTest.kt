package org.yunghegel.gdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode
import org.yunghegel.gdx.renderer.Renderer
import org.yunghegel.gdx.util.*

class ObjRenderingTest : App() {

    lateinit var testKit: TestKit
    lateinit var renderer: Renderer
    lateinit var instance : ModelInstance


    override fun create() {
        var model = SampleModels.TORUS_KNOT.load("models","obj")
        instance = ModelInstance(model)
        renderer = Renderer()
    }

    override fun render() {
        super.render()
        renderer.render(listOf(instance))
    }



}

fun main() {
    ObjRenderingTest().run() {
        this.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32,3,2)
    }
}
