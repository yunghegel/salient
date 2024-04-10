package org.yunghegel.salient.core.graphics.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import org.yunghegel.salient.core.util.ShaderUtils
import kotlin.math.pow

class OutlineDepth
    (private val distanceFalloffEnabled: Boolean) {
    private val shader: ShaderProgram
    var distanceFalloff: Float = 1f
    var size: Float = 2f
    var depthMin: Float = .35f
    var depthMax: Float = .9f
    val insideColor: Color = Color(0f, 0f, 0f, .5f)
    val outsideColor: Color = Color(0f, 0f, 0f, 1f)

    init {
        var prefix = ""
        if (distanceFalloffEnabled) {
            prefix += "#define DISTANCE_FALLOFF\n"
        }
        shader = ShaderProgram(
            Gdx.files.classpath("shaders/outline-depth.vs.glsl").readString(),
            prefix +
                    Gdx.files.classpath("shaders/outline-depth.fs.glsl").readString()
        )
        ShaderUtils.check(shader)
    }

    fun render(batch: SpriteBatch, depthTexture: Texture?, camera: Camera) {
        shader.bind()
        val size = 1 / this.size

        // float depthMin = ui.outlineDepthMin.getValue() * .001f;
        val depthMin = depthMin.pow(10.0f) as Float // 0.35f
        val depthMax = depthMax.pow(10.0f) as Float // 0.9f

        // TODO use an integer instead and divide w and h
        shader.setUniformf("u_size", Gdx.graphics.width * size, Gdx.graphics.height * size)
        shader.setUniformf("u_depth_min", depthMin)
        shader.setUniformf("u_depth_max", depthMax)
        shader.setUniformf("u_inner_color", insideColor)
        shader.setUniformf("u_outer_color", outsideColor)

        if (distanceFalloffEnabled) {
            var d: Float = distanceFalloff
            if (d <= 0) {
                d = .001f
            }
            shader.setUniformf("u_depthRange", camera.far / (camera.near * d))
        }

        batch.shader = shader
        batch.begin()
        batch.draw(depthTexture, 0f, 0f, 1f, 1f, 0f, 0f, 1f, 1f)
        batch.end()
        batch.shader = null
    }
}
