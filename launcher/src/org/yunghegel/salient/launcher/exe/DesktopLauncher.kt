package org.yunghegel.salient.launcher.exe

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import org.yunghegel.gdx.utils.ext.inc
import org.yunghegel.salient.editor.app.Salient
import org.yunghegel.salient.editor.app.configs.Settings
import org.yunghegel.salient.engine.LOAD_SETTINGS
import org.yunghegel.salient.engine.STARTUP
import org.yunghegel.salient.engine.state
import org.yunghegel.salient.engine.helpers.Serializer
import org.yunghegel.salient.engine.system.file.Paths
import org.yunghegel.salient.launcher.config.LaunchOptions
import java.io.File

object DesktopLauncher {

    private val nativeService = NativeService()

    private val settings : Settings
    private val config_file = File("${Paths.USER_HOME}/.salient/salient.config")

//    val daemon : WatchService


    init {


//        daemon = nio.fileDaemon("${Paths.USER_HOME}/.salient")
//

        val yaml = Serializer.yaml
        if (!config_file.exists()) {
            println("Config file not found, creating new one")
            settings = Settings()
            config_file.parentFile.mkdirs()
            config_file.createNewFile()
            config_file.writeText(yaml.encodeToString(Settings.serializer(), settings))
        } else {
            println("Config file found, loading")
            val config = config_file.readText()
            settings = yaml.decodeFromString(Settings.serializer(), config)
        }
        Settings.i = settings

    }



    @JvmStatic
    fun main(args: Array<String>): Unit {
        val config = createConfig()
        config.setWindowListener(nativeService)
        val opts = LaunchOptions(args)
//        opts.whenOption("test") {
//            Lwjgl3Application(TestApplication(), config)
//            return@whenOption
//        }


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
                config.setOpenGLEmulation(gl_emulation, gles_major_version, gles_minor_version)
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