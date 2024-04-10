package org.yunghegel.gdx

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.g3d.ModelBatch
import org.yunghegel.gdx.util.ModelLoadTest
import org.yunghegel.gdx.renderer.shader.PBRShaderProvider

class CustomShaderTest : ModelLoadTest() {

    override fun create() {

        super.create()
        val shaderProvider = PBRShaderProvider()
        batch = ModelBatch(shaderProvider)
    }

}

fun main() {
    Lwjgl3ApplicationConfiguration().apply {
        setTitle("Custom Shader Test")
        setWindowedMode(800, 600)
        setBackBufferConfig(8, 8, 8, 8, 16, 0, 4)
        setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32, 3, 2
        )
    }.let { config ->
        Lwjgl3Application(CustomShaderTest(), config)
    }
}
