package org.yunghegel.gdx.renderer.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderPart
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.glutils.ShaderStage
import org.yunghegel.gdx.renderer.shader.ShaderLoader

fun loadShader(vertPath: String, fragPath: String,geomPath: String,prefix:String = "shaders/"): ShaderProgram {
    val loader = ShaderLoader(Gdx.files.internal(prefix))

    val vertex = loader.load(vertPath)
    val fragment = loader.load(fragPath)
    val geom = loader.load(geomPath)

    val isGL32 = Gdx.app.graphics.isGL32Available
    if(!isGL32) {
        throw IllegalArgumentException("GL32 Required")
    }

    val shaderProgram = ShaderProgram(
        ShaderPart(ShaderStage.vertex,vertex), ShaderPart(ShaderStage.geometry,geom),
        ShaderPart(ShaderStage.fragment,fragment)
    )

    if (!shaderProgram.isCompiled) {
        throw IllegalArgumentException("Error compiling shader: ${shaderProgram.log}")
    }

    return shaderProgram
}

