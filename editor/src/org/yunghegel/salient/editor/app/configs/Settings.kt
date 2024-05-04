package org.yunghegel.salient.editor.app.configs

import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import ktx.async.KtxAsync
import ktx.async.onRenderingThread
import org.yunghegel.salient.editor.app.configs.camera.InputConfiguration
import org.yunghegel.salient.editor.app.configs.graphics.GraphicsConfiguration
import org.yunghegel.salient.editor.app.configs.ui.UIConfig
import org.yunghegel.salient.engine.events.lifecycle.onShutdown
import org.yunghegel.salient.engine.helpers.Serializer
import org.yunghegel.salient.engine.system.file.Paths
import org.yunghegel.salient.modules.io.shared.config.IOConfiguration
import java.io.File

@Serializable
data class Settings(
    var input: InputConfiguration = InputConfiguration(),
    var graphics: GraphicsConfiguration = GraphicsConfiguration(),
    var io: IOConfiguration = IOConfiguration(),
    var ui: UIConfig = UIConfig()
) {



    init {

    }


    @Transient
    val configurations: List<Configuration> = listOf(input, graphics, io, ui)

    fun configure() {
        val yaml = Serializer.yaml
        if (config_file.exists()) {
            val config = config_file.readText()
            val settings = yaml.decodeFromString<Settings>(config)
            input = settings.input
            graphics = settings.graphics
            io = settings.io
            ui = settings.ui
        } else {
            config_file.parentFile.mkdirs()
            config_file.createNewFile()
            config_file.writeText(yaml.encodeToString(this))
        }
    }

    fun save() {
        KtxAsync.launch {
            onRenderingThread {
                val yaml = Serializer.yaml
                configurations.forEach { config->
                    config.sync()
                }

                config_file.writeText(yaml.encodeToString(this))
            }
        }
    }

    companion object {

        val config_file = File("${Paths.USER_HOME}/.salient/salient.config")

        @JvmStatic
        var i: Settings = Settings()
    }
}