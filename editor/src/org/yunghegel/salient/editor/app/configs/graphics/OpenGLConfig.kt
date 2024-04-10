package org.yunghegel.salient.modules.graphics.shared.config

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration.GLEmulation
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration.GLEmulation.*
import org.yunghegel.salient.modules.graphics.shared.config.ColorBufferBits.*
import org.yunghegel.salient.modules.graphics.shared.config.DepthBufferBits.*
import org.yunghegel.salient.modules.graphics.shared.config.StencilBufferBits.*
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
