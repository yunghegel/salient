package org.yunghegel.salient.modules.graphics.shared.config

import com.badlogic.gdx.*
import com.badlogic.gdx.backends.lwjgl3.*
import org.yunghegel.salient.common.shared.config.*
import kotlinx.serialization.Serializable
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.*
import org.yunghegel.salient.modules.ui.edit.EditorFactory
import org.yunghegel.salient.modules.ui.edit.widgets.EnumWidget

@Serializable
data class GraphicsConfiguration(
    var opengl: OpenGLConfig = OpenGLConfig(),
    var video: VideoConfig = VideoConfig(),
    var window: WindowConfig = WindowConfig(),
) : Configuration() {

    init {
//        EditorFactory.register(Resolution::class.java) {
//            EnumWidget(Resolution::class.java).create(it)
//        }
//        EditorFactory.register(DepthBufferBits::class.java) {
//            EnumWidget(DepthBufferBits::class.java).create(it)
//        }
//        EditorFactory.register(StencilBufferBits::class.java) {
//            EnumWidget(StencilBufferBits::class.java).create(it)
//        }
//        EditorFactory.register(AntiAliasing::class.java) {
//            EnumWidget(AntiAliasing::class.java).create(it)
//        }
        registerSyncAction {
            with(window) {
                resolution_width_actual = Gdx.graphics.width
                resolution_height_actual = Gdx.graphics.height
            }
        }

        registerSyncAction {
            with(window) {
                fullscreen = Gdx.graphics.isFullscreen
            }
        }

        registerSyncAction {
            with(window) {
                val intBufferX = BufferUtils.createIntBuffer(1)
                val intBufferY = BufferUtils.createIntBuffer(1)
                val intBufferX1 = BufferUtils.createIntBuffer(1)
                val intBufferY1 = BufferUtils.createIntBuffer(1)
                GLFW.glfwGetWindowPos(windowHandle, intBufferX, intBufferY)
                window_x = intBufferX.get(0)
                window_y = intBufferY.get(0)
                GLFW.glfwGetWindowSize(windowHandle, intBufferX1, intBufferY1)
                resolution_width_actual = intBufferX1.get(0)
                resolution_height_actual = intBufferY1.get(0)

            }
        }
    }

    companion object {

        val windowHandle: Long
            get() = (Gdx.graphics as Lwjgl3Graphics).window.windowHandle
    }

}
