package org.yunghegel.gdx.renderer.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.GdxRuntimeException

class BlinnPhongShader(val renderable :Renderable) : Shader {

    private val loader = ShaderLoader("shaders/")
    private val prefix = ShaderPrefix(renderable)
    private val program : ShaderProgram
    lateinit var context : RenderContext
    internal val attributes : Long

    init {
        program = loader.load(prefix.get(),"blinnphong.vert","blinnphong.frag")
        if (!program.isCompiled) {
            throw GdxRuntimeException(program.log)
        }
        attributes = renderable.material.mask
    }

    override fun dispose() {
        program.dispose()
    }

    override fun init() {

    }

    override fun compareTo(other: Shader?): Int {
        if ((other as BlinnPhongShader).attributes == attributes) return 0
        if ((other.attributes and TextureAttribute.Normal) == 1L) return -1
        return 1
    }

    override fun canRender(instance: Renderable): Boolean {
        return instance.material.mask == attributes
    }

    override fun begin(camera: Camera, context: RenderContext) {
        this.context = context
        program.bind()
        program.setUniformMatrix("u_projViewTrans", camera.combined)
        program.setUniformf("u_cameraPosition", camera.position)

        context.setDepthTest(GL20.GL_LEQUAL)
        context.setCullFace(GL20.GL_BACK)
    }

    override fun render(renderable: Renderable) {

        program.setUniformf("u_dir_light.direction", Vector3(-0.2f,-1f,-0.3f))
        program.setUniformf("u_dir_light.ambient", Vector3(0.2f,0.5f,0.3f))
        program.setUniformf("u_dir_light.diffuse", Vector3(0.2f,0f,0.3f))
        program.setUniformf("u_dir_light.specular", Vector3(0.2f,1f,0.3f))


        program.setUniformMatrix("u_worldTrans", renderable.worldTransform ?: Matrix4())
        prefix.setters.forEach { it(this,renderable) }
        renderable.meshPart.render(program)
    }

    override fun end() {
    }

    companion object : BaseShaderProvider() {
        override fun createShader(renderable: Renderable): Shader {
            return BlinnPhongShader(renderable)
        }
    }
}