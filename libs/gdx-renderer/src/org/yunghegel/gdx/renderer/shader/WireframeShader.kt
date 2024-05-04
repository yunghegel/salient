package org.yunghegel.gdx.renderer.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.glutils.ShaderPart
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShaderStage
import org.lwjgl.opengl.GL11C
import org.lwjgl.opengl.GL20

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
        ShaderProgram.pedantic = false
    }

    override fun begin(camera: com.badlogic.gdx.graphics.Camera,context:RenderContext) {
        super.begin(camera,context)
    }

    override fun render(renderable: Renderable) {
        settings.applyUniforms(program)
        Gdx.graphics.gL20.glEnable(GL11C.GL_POLYGON_OFFSET_FILL)
        Gdx.graphics.gL20.glPolygonOffset(1f, 1f)

        Gdx.graphics.gL20.glEnable(GL20.GL_BLEND)
        program.setUniformf("u_cameraPosition", camera.position)

        super.render(renderable)
    }

    companion object : DefaultShaderProvider() {

        val settings = WireframeSettings()

        override fun createShader(renderable: Renderable): DefaultShader {
            return WireframeShader(renderable)
        }
    }




}