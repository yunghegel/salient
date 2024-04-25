package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import de.damios.guacamole.gdx.graphics.ShaderCompatibilityHelper
import de.damios.guacamole.gdx.graphics.ShaderProgramFactory
import org.apache.commons.lang3.SystemUtils


object Platform {

    enum class OS {
        WINDOWS, LINUX, MAC
    }





    fun createSpriteBatchShader(): ShaderProgram {
        // @formatter:off
        val vertexShader = """#version 150
in vec4 ${ShaderProgram.POSITION_ATTRIBUTE};
in vec4 ${ShaderProgram.COLOR_ATTRIBUTE};
in vec2 ${ShaderProgram.TEXCOORD_ATTRIBUTE}0;
uniform mat4 u_projTrans;
out vec4 v_color;
out vec2 v_texCoords;

void main()
{
   v_color = ${ShaderProgram.COLOR_ATTRIBUTE};
   v_color.a = v_color.a * (255.0/254.0);
   v_texCoords = ${ShaderProgram.TEXCOORD_ATTRIBUTE}0;
   gl_Position =  u_projTrans * ${ShaderProgram.POSITION_ATTRIBUTE};
}
"""
        val fragmentShader = """#version 150
#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP 
#endif
in LOWP vec4 v_color;
in vec2 v_texCoords;
uniform sampler2D u_texture;
out vec4 fragColor;
void main()
{
  fragColor = v_color * texture(u_texture, v_texCoords);
}"""
        // @formatter:on
        return ShaderProgramFactory.fromString(
            vertexShader, fragmentShader,
            true, true
        )
    }

    fun createSpriteBatch(): SpriteBatch {
        return SpriteBatch(
            1000,
            if (ShaderCompatibilityHelper.mustUse32CShader()
            ) createSpriteBatchShader()
            else null
        )
    }

    private fun createImmediateModeRenderer20VertexShader(
        hasNormals: Boolean, hasColors: Boolean, numTexCoords: Int
    ): String {
        var shader = ("""
    #version 150
    in vec4 ${ShaderProgram.POSITION_ATTRIBUTE};
    
    """.trimIndent()
                + (if (hasNormals
        ) "in vec3 " + ShaderProgram.NORMAL_ATTRIBUTE + ";\n"
        else "")
                + (if (hasColors
        ) "in vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
        else ""))

        for (i in 0 until numTexCoords) {
            shader += "in vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + i + ";\n"
        }

        shader += "uniform mat4 u_projModelView;\n"
        shader += (if (hasColors) "out vec4 v_col;\n" else "")

        for (i in 0 until numTexCoords) {
            shader += "out vec2 v_tex$i;\n"
        }

        shader += ("""void main() {
   gl_Position = u_projModelView * ${ShaderProgram.POSITION_ATTRIBUTE};
"""
                + (if (hasColors
        ) "   v_col = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
        else ""))

        for (i in 0 until numTexCoords) {
            shader += ("   v_tex" + i + " = " + ShaderProgram.TEXCOORD_ATTRIBUTE
                    + i + ";\n")
        }
        shader += "   gl_PointSize = 1.0;\n"
        shader += "}\n"
        return shader
    }

    private fun createImmediateModeRenderer20FragmentShader(
        hasNormals: Boolean, hasColors: Boolean, numTexCoords: Int
    ): String {
        var shader = """
            #version 150
            #ifdef GL_ES
            precision mediump float;
            #endif
            
            """.trimIndent()

        if (hasColors) shader += "in vec4 v_col;\n"
        for (i in 0 until numTexCoords) {
            shader += "in vec2 v_tex$i;\n"
            shader += "uniform sampler2D u_sampler$i;\n"
        }

        shader += "out vec4 fragColor;\n"

        shader += """void main() {
   fragColor = ${if (hasColors) "v_col" else "vec4(1, 1, 1, 1)"}"""

        if (numTexCoords > 0) shader += " * "

        for (i in 0 until numTexCoords) {
            shader += if (i == numTexCoords - 1) {
                " texture(u_sampler$i,  v_tex$i)"
            } else {
                " texture(u_sampler$i,  v_tex$i) *"
            }
        }

        shader += ";\n}"
        return shader
    }

    fun createImmediateModeRenderer20DefaultShader(
        hasNormals: Boolean, hasColors: Boolean, numTexCoords: Int
    ): ShaderProgram {
        val vertexShader = createImmediateModeRenderer20VertexShader(
            hasNormals, hasColors, numTexCoords
        )
        val fragmentShader = createImmediateModeRenderer20FragmentShader(
            hasNormals, hasColors, numTexCoords
        )
        return ShaderProgramFactory.fromString(
            vertexShader, fragmentShader,
            true, true
        )
    }

    fun createImmediateModeRenderer20DefaultShader(): ShaderProgram {
        return createImmediateModeRenderer20DefaultShader(false, true, 0)
    }

    fun createShapeRenderer(): ShapeRenderer {
        return ShapeRenderer(
            5000,
            if (ShaderCompatibilityHelper.mustUse32CShader()
            ) createImmediateModeRenderer20DefaultShader()
            else null
        )
    }


}