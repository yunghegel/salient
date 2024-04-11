package org.yunghegel.gdx.renderer.shader

import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider

class PBRShaderProvider : BaseShaderProvider() {

    override fun createShader(renderable: Renderable?): Shader {
        return DefaultShader(renderable!!)
    }

}