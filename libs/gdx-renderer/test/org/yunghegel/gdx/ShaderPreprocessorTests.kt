package org.yunghegel.gdx

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.Renderable
import org.yunghegel.gdx.renderer.shader.DefaultShader
import org.yunghegel.gdx.renderer.shader.ShaderLoader
import org.yunghegel.gdx.util.*
import kotlin.test.Test

class ShaderPreprocessorTests {

    @Test fun `test load shader`() {
        TestApplication(createContext = {
            val shader = ShaderLoader.load(Gdx.files.internal("shaders/"), "default.vert")
            println(shader)
        }, render = {}).launch()


    }



}