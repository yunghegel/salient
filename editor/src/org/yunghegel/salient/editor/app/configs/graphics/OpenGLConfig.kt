package org.yunghegel.salient.editor.app.configs.graphics

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration.GLEmulation
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration.GLEmulation.GL32
import kotlinx.serialization.Serializable
import org.yunghegel.gdx.utils.reflection.Editable
import org.yunghegel.salient.editor.app.configs.graphics.ColorBufferBits.RGB888
import org.yunghegel.salient.editor.app.configs.graphics.DepthBufferBits._24
import org.yunghegel.salient.editor.app.configs.graphics.StencilBufferBits._8
import org.yunghegel.salient.engine.ui.widgets.value.EditorFactory
import org.yunghegel.salient.engine.ui.widgets.value.widgets.EnumWidget

@Serializable
data class OpenGLConfig(
    @Editable(name = "GLVersion",readonly =true) val gl_emulation: GLEmulation = GL32,
    val gles_minor_version: Int = 2,
    val gles_major_version: Int = 3,
    val depth_bits: DepthBufferBits = _24,
    val stencil_bits: StencilBufferBits = _8,
    val color_bits: ColorBufferBits = RGB888,
) {
    init {
        EditorFactory.register(DepthBufferBits::class.java) {
            EnumWidget(DepthBufferBits::class.java).create(it)
        }
        EditorFactory.register(StencilBufferBits::class.java) {
            EnumWidget(StencilBufferBits::class.java).create(it)
        }
        EditorFactory.register(ColorBufferBits::class.java) {
            EnumWidget(ColorBufferBits::class.java).create(it)
        }
        EditorFactory.register(GLEmulation::class.java) {
            EnumWidget(GLEmulation::class.java).create(it)
        }
    }
}
