package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import org.yunghegel.salient.engine.helpers.TextRenderer.camera
import kotlin.math.pow

data class OutlineSettings (
    var distanceFalloff: Float = 1f,
    var size: Float = 2f,
    var depthMin: Float = .35f,
    var depthMax: Float = .9f,
    val insideColor: Color = Color(0f, 0f, 0f, 0f),
    val outsideColor: Color = Color(1f, 1f, 1f, 1f),
    var distanceFalloffEnabled: Boolean = false
) {

    fun applyUniforms(shader: ShaderProgram) = shader.apply {
        size = 1 / size
        depthMin = depthMin.pow(10.0f) // 0.35f
        depthMax = depthMax.pow(10.0f) // 0.9f


        setUniformf("u_size", Gdx.graphics.width * size, Gdx.graphics.height * size)
        setUniformf("u_depth_min", depthMin)
        setUniformf("u_depth_max", depthMax)
        setUniformf("u_inner_color", insideColor)
        setUniformf("u_outer_color", outsideColor)

        if (distanceFalloffEnabled) {
            var d: Float = distanceFalloff
            if (d <= 0) {
                d = .001f
            }
            setUniformf("u_depthRange", camera.far / (camera.near * d))
        }
    }


}
