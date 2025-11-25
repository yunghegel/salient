package org.yunghegel.gdx.renderer.shader

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.GdxRuntimeException
import org.yunghegel.gdx.renderer.env.DirectionalLight
import org.yunghegel.gdx.renderer.env.PointLight
import org.yunghegel.gdx.renderer.env.SpotLight

open class DefaultShader(val renderable: Renderable) : Shader {

    val program : ShaderProgram

    lateinit var context : RenderContext
        private set

    val attributes : Long

    val loader = ShaderLoader(Gdx.files.internal("shaders/"))

    var prefix = ShaderPrefix("",renderable)

    // Lighting properties
    var directionalLight: DirectionalLight = DirectionalLight()
    var pointLights: com.badlogic.gdx.utils.Array<PointLight> = com.badlogic.gdx.utils.Array()
    var spotLights: com.badlogic.gdx.utils.Array<SpotLight> = com.badlogic.gdx.utils.Array()
    var cameraPosition: Vector3 = Vector3()
    var shininess: Float = 32.0f
    private val normalMatrix = Matrix3()


    init {

        program = loader.load(prefix.prefix,"default.vert","default.frag")
        if (!program.isCompiled) {
            throw GdxRuntimeException("Couldn't compile shader: " + program.log)
        }
        attributes = renderable.material.mask

        
        // Add lighting uniform setters
        setupLightingSetters()
    }
    
    private fun setupLightingSetters() {
        // Directional light setter
        prefix.addSetter { shader, _ ->
            if (shader is DefaultShader && shader.directionalLight != null) {
                val light = shader.directionalLight!!
                shader.program.setUniformf("u_directionalLight.direction", light.direction)
                shader.program.setUniformf("u_directionalLight.ambient", light.ambient)
                shader.program.setUniformf("u_directionalLight.diffuse", light.diffuse)
                shader.program.setUniformf("u_directionalLight.specular", light.specular)
            }
        }
        
        // Point lights setter
        prefix.addSetter { shader, _ ->
            if (shader is DefaultShader) {
                for (i in 0 until minOf(shader.pointLights.size, 3)) {
                    val light = shader.pointLights[i]
                    shader.program.setUniformf("u_pointLight[$i].position", light.position)
                    shader.program.setUniformf("u_pointLight[$i].constant", light.constant)
                    shader.program.setUniformf("u_pointLight[$i].linear", light.linear)
                    shader.program.setUniformf("u_pointLight[$i].quadratic", light.quadratic)
                    shader.program.setUniformf("u_pointLight[$i].ambient", light.color.cpy().scl(0.1f))
                    shader.program.setUniformf("u_pointLight[$i].diffuse", light.color)
                    shader.program.setUniformf("u_pointLight[$i].specular", light.color)
                }
            }
        }
        
        // Spot lights setter
        prefix.addSetter { shader, _ ->
            if (shader is DefaultShader) {
                for (i in 0 until minOf(shader.spotLights.size, 3)) {
                    val light = shader.spotLights[i]
                    shader.program.setUniformf("u_spotLight[$i].position", light.position)
                    shader.program.setUniformf("u_spotLight[$i].direction", light.direction)
                    shader.program.setUniformf("u_spotLight[$i].cutOff", Math.cos(Math.toRadians(light.cutOff.toDouble())).toFloat())
                    shader.program.setUniformf("u_spotLight[$i].outerCutOff", Math.cos(Math.toRadians(light.outerCutOff.toDouble())).toFloat())
                    shader.program.setUniformf("u_spotLight[$i].constant", light.constant)
                    shader.program.setUniformf("u_spotLight[$i].linear", light.linear)
                    shader.program.setUniformf("u_spotLight[$i].quadratic", light.quadratic)
                    shader.program.setUniformf("u_spotLight[$i].ambient", light.color.cpy().scl(0.1f))
                    shader.program.setUniformf("u_spotLight[$i].diffuse", light.color)
                    shader.program.setUniformf("u_spotLight[$i].specular", light.color)
                }
            }
        }
        
        // Camera position setter
        prefix.addSetter { shader, _ ->
            if (shader is DefaultShader) {
                shader.program.setUniformf("u_cameraPosition", shader.cameraPosition)
            }
        }
        
        // Normal matrix setter
        prefix.addSetter { shader, renderable ->
            if (shader is DefaultShader && renderable != null) {
                val worldTransform = renderable.worldTransform ?: Matrix4()
                // Extract the upper-left 3x3 matrix for normal transformation
                // The normal matrix should be the inverse transpose of the world matrix's 3x3 portion
                val tempMatrix = Matrix4(worldTransform)
                tempMatrix.inv()
                shader.normalMatrix.set(tempMatrix).transpose()
                shader.program.setUniformMatrix("u_normalMatrix", shader.normalMatrix)
            }
        }
        
        // Shininess setter
//        prefix.addSetter { shader, _ ->
//            if (shader is DefaultShader) {
//                shader.program.setUniformf("u_shininess", shader.shininess)
//            }
//        }
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
        
        // Set camera position for lighting calculations
        cameraPosition.set(camera.position)

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