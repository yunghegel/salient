package org.yunghegel.gdx.renderer.shader

import com.badlogic.gdx.graphics.g3d.shaders.BaseShader
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector3
import kotlin.math.pow

data class WireframeSettings(
    val alphaCutoff: Float =0.25f,
    val lineColor : Vector3=Vector3(1f,1f,0f),
    val fillColor : Vector3=Vector3(0f,0f,0f),
    val alpha : Float = 0.5f,
    val useDistanceFalloff: Boolean = true
) {

    fun applyUniforms(shader: ShaderProgram) = shader.apply {
        setUniformf("u_alphaCutoff",alphaCutoff)
        setUniformf("u_wirecolor",lineColor)
        setUniformf("u_fillcolor",fillColor)
        setUniformf("u_alpha",alpha)
        setUniformi("u_useDistanceFalloff",if(useDistanceFalloff) 1 else 0)
    }


}
