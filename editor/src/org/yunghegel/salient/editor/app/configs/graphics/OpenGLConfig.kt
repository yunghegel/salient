package org.yunghegel.salient.editor.app.configs.graphics

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration.GLEmulation
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration.GLEmulation.*
import org.yunghegel.salient.editor.app.configs.graphics.ColorBufferBits.*
import org.yunghegel.salient.editor.app.configs.graphics.DepthBufferBits.*
import org.yunghegel.salient.editor.app.configs.graphics.StencilBufferBits.*
import kotlinx.serialization.Serializable

@Serializable
data class OpenGLConfig(
    val gl_emulation: GLEmulation = GL32,
    val gles_minor_version: Int = 2,
    val gles_major_version: Int = 3,
    val depth_bits: DepthBufferBits = _24,
    val stencil_bits: StencilBufferBits = _8,
    val color_bits: ColorBufferBits = RGB888,
)
