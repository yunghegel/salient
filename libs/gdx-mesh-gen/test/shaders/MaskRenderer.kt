package shaders

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.Array

object MaskRenderer {

    val maskBatch = ModelBatch(MaskShader.provider)
    lateinit var texture : Texture
    lateinit var region : TextureRegion

    var fbo: FrameBuffer = FrameBuffer(
        Pixmap.Format.RGBA8888,
        Gdx.graphics.width,
        Gdx.graphics.height,
        true
    )

    fun ensureFBO(width: Int, height: Int) : FrameBuffer{
        if (fbo.width != width || fbo.height != height) {
            fbo.dispose()
            fbo = FrameBuffer(Pixmap.Format.RGBA8888, width, height, true)
        }
        return fbo
    }

    fun render(renderableProviders:Array<RenderableProvider>, camera: Camera) {
        fbo = ensureFBO(Gdx.graphics.width, Gdx.graphics.height)
        fbo.begin()
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        maskBatch.begin(camera)
        maskBatch.render(renderableProviders)
        maskBatch.end()
        fbo.end()

        texture = fbo.colorBufferTexture
        region = TextureRegion(texture)
        region.flip(false,true)
    }

    fun dispose(){
        maskBatch.dispose()
        fbo.dispose()
    }

    fun renderIntoTexture(renderableProvider: RenderableProvider, camera: Camera) : TextureRegion {
        val renderableProviders = Array<RenderableProvider>()
        renderableProviders.add(renderableProvider)
        render(renderableProviders, camera)
        return region
    }


    private class MaskShader(renderable: Renderable) : DefaultShader(renderable, config) {


        val VERTEX_SHADER =
            "attribute vec4 a_position;\n" +
                    "uniform mat4 u_projViewTrans;\n" +
                    "uniform mat4 u_worldTrans;\n" +
                    "\n" +
                    "void main() {\n" +
                    "    gl_Position = u_projViewTrans * u_worldTrans * a_position;\n" +
                    "}"

        val FRAGMENT_SHADER =
            "void main() {\n" +
                    "gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);\n" +
                    "}"


        init {
            program = ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER)
            if (!program.isCompiled) {
                println(program.log)
            }
        }


        val UNIFORM_PROJ_VIEW_TRANS = register("u_projViewTrans")
        val UNIFORM_WORLD_TRANS = register("u_worldTrans")

        override fun compareTo(other: Shader?): Int {
            return 0
        }

        override fun canRender(instance: Renderable?): Boolean {
            return true
        }

        override fun render(renderable: Renderable?) {
            set(UNIFORM_WORLD_TRANS, renderable!!.worldTransform)
            set(UNIFORM_PROJ_VIEW_TRANS, camera.combined)

            renderable.meshPart.render(program)
        }

        companion object {
            val provider = object : BaseShaderProvider() {
                override fun createShader(renderable: Renderable?): Shader {
                    return MaskShader(renderable!!)
                }
            }
            val config = Config(
                Gdx.files.internal("shaders/mask.vert").readString(),
                Gdx.files.internal("shaders/mask.frag").readString()
            )
        }

    }


}

