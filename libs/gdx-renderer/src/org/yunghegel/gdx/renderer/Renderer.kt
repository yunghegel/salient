package org.yunghegel.gdx.renderer

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.g3d.utils.*
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.ScreenUtils
import org.yunghegel.gdx.renderer.env.Light
import org.yunghegel.gdx.renderer.mrt.TextureAttachment
import org.yunghegel.gdx.renderer.mrt.TextureAttachment.Companion.toFbo
import org.yunghegel.gdx.renderer.util.FullScreenQuad
import org.yunghegel.gdx.renderer.util.RenderablePool
import org.yunghegel.gdx.renderer.util.maskOf

class Renderer : Disposable {

    var camera : Camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()).apply {
        position.set(0f, 0f, 10f)
        lookAt(0f, 0f, 0f)
        near = .1f
        far = 300f
        update()
    }

    val shaderProvider : BaseShaderProvider

    val renderableSorter : DefaultRenderableSorter

    var mrtSceneShader: ShaderProgram

    val quad : Mesh = FullScreenQuad()

    val frameBuffer:FrameBuffer

    val batch : SpriteBatch = SpriteBatch()

    val modelCache: ModelCache = ModelCache()

    var lights  = Array<Light>().apply {
        val modelBuilder = ModelBuilder()

        for (i in 0 until 10) {
            modelBuilder.begin()

            val light = Light()
            light.color[MathUtils.random(.1f), MathUtils.random(.1f)] = MathUtils.random(.1f)
            light.position[MathUtils.random(-10f, 10f), MathUtils.random(10f, 15f)] = MathUtils.random(-10f, 10f)
            light.vy = MathUtils.random(10f, 20f)
            light.vx = MathUtils.random(-10f, 10f)
            light.vz = MathUtils.random(-10f, 10f)

            val meshPartBuilder: MeshPartBuilder = modelBuilder.part(
                "light",
                GL20.GL_TRIANGLES,
                (VertexAttributes.Usage.Position or VertexAttributes.Usage.ColorPacked or VertexAttributes.Usage.Normal).toLong(),
                Material()
            )
            meshPartBuilder.setColor(light.color.x, light.color.y, light.color.z, 1f)
            meshPartBuilder.sphere(0.2f, 0.2f, 0.2f, 10, 10)

            light.lightInstance = ModelInstance(modelBuilder.end())
            add(light)
        }
    }

    var renderables = Array<Renderable>()

    var renerablePool =  RenderablePool()


    init {

        ShaderProgram.pedantic = false

        ShaderProgram.prependVertexCode =
            if (Gdx.app.type == Application.ApplicationType.Desktop) "#version 140\n #extension GL_ARB_explicit_attrib_location : enable\n"
            else "#version 300 es\n"
        ShaderProgram.prependFragmentCode =
            if (Gdx.app.type == Application.ApplicationType.Desktop) "#version 140\n #extension GL_ARB_explicit_attrib_location : enable\n"
            else "#version 300 es\n"

        shaderProvider = object : BaseShaderProvider() {
            override fun createShader(renderable: Renderable): Shader {
                return MRTShader(renderable)
            }
        }
        renderableSorter = object : DefaultRenderableSorter() {
            override fun compare(o1: Renderable, o2: Renderable): Int {
                return o1.shader.compareTo(o2.shader)
            }
        }

        maskOf(
            TextureAttachment.DIFFUSE,
            TextureAttachment.NORMAL,
            TextureAttachment.POSITION,
            TextureAttachment.DEPTH,
        ).apply {
            toFbo(this).also {
                frameBuffer = it
            }
        }
//        val frameBufferBuilder = FrameBufferBuilder(
//            Gdx.graphics.width,
//            Gdx.graphics.height
//        )
//        frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGBA8, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE)
//        frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGB8, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE)
//        frameBufferBuilder.addColorTextureAttachment(GL30.GL_RGB8, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE)
//        frameBufferBuilder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT16, GL30.GL_UNSIGNED_SHORT)
//
//        frameBuffer = frameBufferBuilder.build()

        mrtSceneShader = ShaderProgram(
            Gdx.files.internal("shaders/mrtscene.vert"),
            Gdx.files.internal("shaders/mrtscene.frag")
        )
        if (!mrtSceneShader.isCompiled) {
            println(mrtSceneShader.log)
        }



    }

    var track = 0f

    fun render(instances : List<RenderableProvider>) {
        track += Gdx.graphics.deltaTime

        ScreenUtils.clear(0f, 0f, 0f, 1f, true)

        begin()

        frameBuffer.begin()
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        renerablePool.flush()
        renderables.clear()

        modelCache.begin(camera)
        for (instance in instances) {
            if (instance is ModelInstance) {
                modelCache.add(instance)
                instance.getRenderables(renderables, renerablePool)
            }
        }
        for (light in lights) {
            light.update(Gdx.graphics.deltaTime)
            modelCache.add(light.lightInstance)
        }
        modelCache.end()
        modelCache.getRenderables(renderables, renerablePool)

        for (renderable in renderables) {
            renderable.shader = shaderProvider.getShader(renderable)
        }

        renderableSorter.sort(camera, renderables)
        var currentShader: Shader? = null
        for (i in 0 until renderables.size) {
            val renderable = renderables[i]
            if (currentShader !== renderable.shader) {
                currentShader?.end()
                currentShader = renderable.shader
                currentShader.begin(camera, Renderer)
            }
            currentShader!!.render(renderable)
        }
        currentShader?.end()

        frameBuffer.end()

        mrtSceneShader.bind()
        mrtSceneShader.setUniformi(
            "u_diffuseTexture",
            textureBinder.bind(frameBuffer.textureAttachments[0])
        )
        mrtSceneShader.setUniformi(
            "u_normalTexture",
            textureBinder.bind(frameBuffer.textureAttachments[1])
        )
        mrtSceneShader.setUniformi(
            "u_positionTexture",
            textureBinder.bind(frameBuffer.textureAttachments[2])
        )
        mrtSceneShader.setUniformi(
            "u_depthTexture",
            textureBinder.bind(frameBuffer.textureAttachments[3])
        )
        for (i in 0 until lights.size) {
            val light = lights[i]
            mrtSceneShader.setUniformf("lights[$i].lightPosition", light.position)
            mrtSceneShader.setUniformf("lights[$i].lightColor", light.color)
        }
        mrtSceneShader.setUniformf("u_viewPos", camera.position)
        mrtSceneShader.setUniformMatrix("u_inverseProjectionMatrix", camera.invProjectionView)
        quad.render(mrtSceneShader, GL30.GL_TRIANGLE_FAN)
        end()

        batch.disableBlending()
        batch.begin()
        batch.draw(
            frameBuffer.textureAttachments[TextureAttachment.DIFFUSE.layout],
            0f,
            0f,
            Gdx.graphics.width / 4f,
            Gdx.graphics.height / 4f,
            0f,
            0f,
            1f,
            1f
        )
        batch.draw(
            frameBuffer.textureAttachments[TextureAttachment.NORMAL.layout],
            Gdx.graphics.width / 4f,
            0f,
            Gdx.graphics.width / 4f,
            Gdx.graphics.height / 4f,
            0f,
            0f,
            1f,
            1f
        )
        batch.draw(
            frameBuffer.textureAttachments[TextureAttachment.POSITION.layout],
            2 * Gdx.graphics.width / 4f,
            0f,
            Gdx.graphics.width / 4f,
            Gdx.graphics.height / 4f,
            0f,
            0f,
            1f,
            1f
        )
        batch.draw(
            frameBuffer.textureAttachments[TextureAttachment.DEPTH.layout],
            3 * Gdx.graphics.width / 4f,
            0f,
            Gdx.graphics.width / 4f,
            Gdx.graphics.height / 4f,
            0f,
            0f,
            1f,
            1f
        )
        batch.end()
    }

    override fun dispose() {
        frameBuffer.dispose()
        batch.dispose()


        for (light in lights) {
            light.lightInstance?.model?.dispose()
        }
        mrtSceneShader.dispose()
        quad.dispose()
    }


    companion object : RenderContext(DefaultTextureBinder(DefaultTextureBinder.ROUNDROBIN, 1))

}

internal class MRTShader(renderable: Renderable) : Shader {
    var shaderProgram: ShaderProgram
    var attributes: Long

    var context: RenderContext? = null

    var matrix3: Matrix3 = Matrix3()

    init {
        var prefix = ""
        if (renderable.material.has(TextureAttribute.Normal)) {
            prefix += "#define texturedFlag\n"
        }

        val vert = Gdx.files.internal("shaders/mrt.vert").readString()
        val frag = Gdx.files.internal("shaders/mrt.frag").readString()
        shaderProgram = ShaderProgram(prefix + vert, prefix + frag)

        if (!shaderProgram.isCompiled) {
            throw GdxRuntimeException(shaderProgram.log)
        }
        attributes = renderable.material.mask
    }

    override fun init() {
    }

    override fun compareTo(other: Shader): Int {
        // quick and dirty shader sort
        if ((other as MRTShader).attributes == attributes) return 0
        if ((other.attributes and TextureAttribute.Normal) == 1L) return -1
        return 1
    }

    override fun canRender(instance: Renderable): Boolean {
        return attributes == instance.material.mask
    }

    override fun begin(camera: Camera, context: RenderContext) {
        this.context = context
        shaderProgram.bind()
        shaderProgram.setUniformMatrix("u_projViewTrans", camera.combined)
        context.setDepthTest(GL20.GL_LEQUAL)
        context.setCullFace(GL20.GL_BACK)
    }

    override fun render(renderable: Renderable) {
        val material = renderable.material

        val diffuseTexture = material[TextureAttribute.Diffuse]
        val normalTexture = material[TextureAttribute.Normal]
        val specTexture = material[TextureAttribute.Specular]

        if (diffuseTexture != null) {
            shaderProgram.setUniformi(
                "u_diffuseTexture",
                context!!.textureBinder.bind((diffuseTexture as TextureAttribute).textureDescription.texture)
            )
        }
        if (normalTexture != null) {
            shaderProgram.setUniformi(
                "u_normalTexture",
                context!!.textureBinder.bind((normalTexture as TextureAttribute).textureDescription.texture)
            )
        }
        if (specTexture != null) {
            shaderProgram.setUniformi(
                "u_specularTexture",
                context!!.textureBinder.bind((specTexture as TextureAttribute).textureDescription.texture)
            )
        }

        shaderProgram.setUniformMatrix("u_worldTrans", renderable.worldTransform)
        shaderProgram.setUniformMatrix("u_normalMatrix", matrix3.set(renderable.worldTransform).inv().transpose())

        renderable.meshPart.render(shaderProgram)
    }

    override fun end() {
    }

    override fun dispose() {
        shaderProgram.dispose()
    }
}