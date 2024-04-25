package org.yunghegel.salient.launcher

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import org.yunghegel.salient.editor.app.Salient
import org.yunghegel.salient.editor.app.configs.Settings
import org.yunghegel.salient.editor.app.storage.persistent
import org.yunghegel.salient.engine.events.lifecycle.onStartup
import org.yunghegel.salient.engine.helpers.reflect.Type
import org.yunghegel.salient.engine.helpers.reflect.annotation.Key
import org.yunghegel.salient.engine.io.debug

object DesktopLauncher {

    val nativeService = NativeService()

    init {
        Settings.i.configure()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val config = createConfig()
        config.setWindowListener(nativeService)
//        val salient = Salient()
        Lwjgl3Application(Salient(), config).apply {
            addLifecycleListener(nativeService)
        }
    }

    private fun createConfig(): Lwjgl3ApplicationConfiguration {
        val config = Lwjgl3ApplicationConfiguration()

        with(Settings.i) {
            with(graphics.window) {
                config.setWindowedMode(resolution_width_actual, resolution_height_actual)
                config.setAutoIconify(auto_iconify)
                config.setDecorated(decorated)
                config.setResizable(resizable)
                config.setTitle(title)
                config.setWindowIcon(icon_path)

                if (window_x != -1 && window_y != -1) config.setWindowPosition(window_x, window_y)
            }
            with(graphics.opengl) {
                config.setBackBufferConfig(
                    color_bits.r,
                    color_bits.g,
                    color_bits.b,
                    0,
                    depth_bits.value,
                    stencil_bits.value,
                    graphics.video.antiAliasing.value
                )
//                config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 2)
            }
            with(graphics.video) {
                config.setIdleFPS(idle_fps)
                config.setForegroundFPS(fps_cap)
                config.useVsync(vsync)
            }

        }
        return config
    }


}