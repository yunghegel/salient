package org.yunghegel.gdx.renderer.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider
import com.badlogic.gdx.graphics.glutils.ShaderPart
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShaderStage

class WireframeShader(renderable: Renderable) : DefaultShader(renderable) {

    val loader = ShaderLoader(Gdx.files.internal("shaders/"))
    val prefix = ShaderPrefix("",renderable)

    val vertex = loader.load("wireframe.vert")

    val fragment = loader.load("wireframe.frag")

    val geom = loader.load("wireframe.geom")

    fun validate() {
        val sb=StringBuilder()
        sb.appendLine("Vertex:\n" + vertex)
        sb.appendLine("Fragment:\n" + fragment)
        sb.appendLine("Geometry:\n" + geom)
        println(sb.toString())
    }

    init {

        val isGL32 = Gdx.app.graphics.isGL32Available
        if(!isGL32) {
            throw IllegalArgumentException("GL32 Required")
        }

        val shaderProgram = ShaderProgram(ShaderPart(ShaderStage.vertex,vertex),ShaderPart(ShaderStage.geometry,geom),
            ShaderPart(ShaderStage.fragment,fragment)
        )

        if (!shaderProgram.isCompiled) {
            throw IllegalArgumentException("Error compiling shader: ${shaderProgram.log}")
        }
        program = shaderProgram
    }

    companion object : DefaultShaderProvider() {
        override fun createShader(renderable: Renderable): DefaultShader {
            return WireframeShader(renderable)
        }
    }




}