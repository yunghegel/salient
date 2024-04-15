package org.yunghegel.gdx.util

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import org.yunghegel.gdx.renderer.Renderer

class MRTTest  : ModelLoadTest() {

    lateinit var renderer: Renderer

    override fun create() {
        super.create()

        renderer = Renderer()
        renderer.camera = this.cam

    }

    override fun render() {
        super.render()

        renderer.render(instances)
    }
    
}

fun main(args: Array<String>) {
    MRTTest().launch { cfg -> cfg.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32,3,2)}
}