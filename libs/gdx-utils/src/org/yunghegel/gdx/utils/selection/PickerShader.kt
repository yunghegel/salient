package org.yunghegel.gdx.utils.selection

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector3

class PickerShader : BaseShader() {

    private val UNIFORM_PROJ_VIEW_MATRIX: Int = register(Uniform("u_projViewMatrix"))
    private val UNIFORM_TRANS_MATRIX: Int = register(Uniform("u_transMatrix"))
    private val UNIFORM_COLOR: Int = register(Uniform("u_color"))

    init {
        program = ShaderProgram(VERTEX_SHADER, FRAGMENT_SHADER)
    }

    override fun init() {
        super.init(program, null)
    }

    override fun compareTo(other: Shader): Int {
        return 0
    }

    override fun canRender(instance: Renderable): Boolean {
        if (instance.material[PickerIDAttribute.Type]==null) return false
        return true
    }

    override fun begin(camera: Camera, context: RenderContext) {
        this.context = context
        this.context.setCullFace(GL20.GL_BACK)
        this.context.setDepthTest(GL20.GL_LEQUAL, 0f, 1f)
        this.context.setDepthMask(true)

        program.bind()

        set(UNIFORM_PROJ_VIEW_MATRIX, camera.combined)
    }

    override fun render(renderable: Renderable) {
        set(UNIFORM_TRANS_MATRIX, renderable.worldTransform)
        Gdx.gl.glLineWidth(5f)
        val goID = renderable.material[PickerIDAttribute.Type] as PickerIDAttribute?
        if (goID != null) {
            set(UNIFORM_COLOR, vec3.set(goID.r.toFloat(), goID.g.toFloat(), goID.b.toFloat()))
        }

        renderable.meshPart.render(program)
        Gdx.gl.glLineWidth(1f)
    }

    override fun end() {
        program.end()
    }

    override fun dispose() {
        program.dispose()
    }

    companion object : DefaultShaderProvider() {

        override fun getShader(renderable: Renderable?): Shader {
            return instance!!
        }

        override fun createShader(renderable: Renderable?): Shader {
            return instance!!
        }

        var instance: PickerShader? = null
            get() {
                if (field == null) {
                    field = PickerShader()
                    field!!.init()
                }
                return field
            }
            private set

        private const val VERTEX_SHADER = ("attribute vec3 a_position;" + "uniform mat4 u_transMatrix;"
                + "uniform mat4 u_projViewMatrix;" + "void main(void) {"
                + "vec4 worldPos = u_transMatrix * vec4(a_position, 1.0);" + "gl_Position = u_projViewMatrix * worldPos;"
                + "}")

        private const val FRAGMENT_SHADER = ("#ifdef GL_ES\n" + "precision highp float;\n" + "#endif \n"
                + "uniform vec3 u_color;" + "void main(void) {\n"
                + "gl_FragColor = vec4(u_color.r/255.0, u_color.g/255.0, u_color.b/255.0, 1.0);" + "}")

        private val vec3 = Vector3()
    }
}