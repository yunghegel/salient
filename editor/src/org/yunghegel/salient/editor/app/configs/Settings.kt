package org.yunghegel.salient.editor.app.configs

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.yunghegel.salient.editor.app.configs.camera.InputConfiguration
import org.yunghegel.salient.editor.app.configs.graphics.GraphicsConfiguration
import org.yunghegel.salient.editor.app.configs.ui.UIConfig
import org.yunghegel.salient.editor.app.storage.Serializer
import org.yunghegel.salient.engine.events.lifecycle.onShutdown
import org.yunghegel.salient.engine.io.Paths
import org.yunghegel.salient.modules.io.shared.config.IOConfiguration

@Serializable
data class Settings(
    var input: InputConfiguration = InputConfiguration(),
    var graphics: GraphicsConfiguration = GraphicsConfiguration(),
    var io: IOConfiguration = IOConfiguration(),
    var ui: UIConfig = UIConfig()
) {

    init {
        onShutdown {
            save()
        }
    }

    @Transient
    val config_file = Paths.CONFIG_FILEPATH.handle.file()

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
        println("Settings: ${yaml.encodeToString(this)}}")
    }

    fun save() {
        println("Saving settings...")
        val yaml = Serializer.yaml
        configurations.forEach { it.sync() }

        config_file.writeText(yaml.encodeToString(this))
    }

    companion object {

        @JvmStatic
        val i: Settings by lazy { Settings().apply { configure() } }
    }
}