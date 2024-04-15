package org.yunghegel.gdx.renderer.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.utils.GdxRuntimeException

open class DefaultShader(val renderable: Renderable) : Shader {

    val program : ShaderProgram

    lateinit var context : RenderContext
        private set

    val attributes : Long

    val loader = ShaderLoader(Gdx.files.internal("shaders/"))

    var prefix = ShaderPrefix("",renderable)

    val setters : MutableList<(DefaultShader,Renderable)->Unit>


    init {
        setters = mutableListOf()

        program = loader.load(prefix.prefix,"default.vert","default.frag")
        if (!program.isCompiled) {
            throw GdxRuntimeException("Couldn't compile shader: " + program.log)
        }
        attributes = renderable.material.mask
    }

    override fun dispose() {
        program.dispose()
    }

    override fun init() {

    }

    override fun compareTo(other: Shader): Int {
        if ((other as DefaultShader).attributes == attributes) return 0
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

        context.setDepthTest(GL20.GL_LEQUAL)
        context.setCullFace(GL20.GL_BACK)

    }

    override fun render(renderable: Renderable) {
        val material = renderable.material

        val diffuseTexture = material[TextureAttribute.Diffuse]
        val normalTexture = material[TextureAttribute.Normal]
        val specTexture = material[TextureAttribute.Specular]

        if (diffuseTexture != null) {
            val loc = program.getUniformLocation("u_diffuseTexture")
            program.setUniformi(loc, context.textureBinder.bind((diffuseTexture as TextureAttribute).textureDescription.texture))
        }
        if (normalTexture != null) {
            val loc = program.getUniformLocation("u_normalTexture")
            program.setUniformi(loc, context.textureBinder.bind((normalTexture as TextureAttribute).textureDescription.texture))
        }
        if (specTexture != null) {
            val loc = program.getUniformLocation("u_specularTexture")
            program.setUniformi(loc, context.textureBinder.bind((specTexture as TextureAttribute).textureDescription.texture))
        }

        program.setUniformMatrix("u_worldTrans", renderable.worldTransform ?: Matrix4())

        prefix.setters.forEach { it(this,renderable) }


        renderable.meshPart.render(program)

    }

    override fun end() {
    }

}