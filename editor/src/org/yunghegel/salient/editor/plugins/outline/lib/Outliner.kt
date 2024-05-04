package org.yunghegel.salient.editor.plugins.outline.lib

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import org.yunghegel.gdx.utils.ext.draw
import org.yunghegel.salient.engine.graphics.OutlineSettings

class Outliner {

    val shader: ShaderProgram
    val batch = SpriteBatch()

    init {
        var prefix = ""
        if (settings.distanceFalloffEnabled) {
            prefix += "#define DISTANCE_FALLOFF\n"
        }
        shader = ShaderProgram(
            Gdx.files.classpath("shaders/outline-depth.vs.glsl").readString(),
            prefix +
                    Gdx.files.classpath("shaders/outline-depth.fs.glsl").readString()
        )
        if (!shader.isCompiled) {
            println(shader.log)
        }
    }

    fun renderOutline(depth: Texture?) {
        shader.bind()
        settings.applyUniforms(shader)

        batch.shader = shader
        depth?.draw(batch)
        batch.shader = null

    }

    companion object {
        val settings = OutlineSettings()
    }
}