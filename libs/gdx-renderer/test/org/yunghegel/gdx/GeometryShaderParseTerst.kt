package org.yunghegel.gdx

import com.badlogic.gdx.graphics.g3d.Renderable
import org.yunghegel.gdx.renderer.shader.WireframeShader
import org.yunghegel.gdx.util.SampleModels
import org.yunghegel.gdx.util.TestApplication
import org.yunghegel.gdx.util.launch
import org.yunghegel.gdx.util.toInstance
import kotlin.test.Test

class GeometryShaderParseTerst {

    @Test
    fun test_parse_geometry_shader() {

        TestApplication(createContext = {

            val shader = WireframeShader(SampleModels.random().toInstance().getRenderable(Renderable()  ))
            shader.validate()

        }, render = {


        }
        ).run()

    }

}