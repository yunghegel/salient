package org.yunghegel.salient.modules.graphics.shared.config

import com.badlogic.gdx.*
import org.yunghegel.salient.modules.graphics.shared.config.Resolution.*
import org.yunghegel.salient.modules.io.*
import kotlinx.serialization.Serializable

@Serializable
data class WindowConfig(
    var resolution_preset: Resolution = _1920x1080,
    var resolution_width_actual: Int = resolution_preset.width,
    var resolution_height_actual: Int = resolution_preset.height,
    var window_x: Int = -1,
    var window_y: Int = -1,
    var fullscreen: Boolean = false,
    var auto_iconify: Boolean = false,
    var decorated: Boolean = true,
    var resizable: Boolean = true,
    var icon_path: String = "icon.png",
    var title: String = "Salient"
) {

    init {

        onStartup {
            resolution_preset = resolveResolution()
            resolution_width_actual = Gdx.graphics.backBufferWidth
            resolution_height_actual = Gdx.graphics.backBufferHeight
        }
    }

    fun resolveResolution(): Resolution {
        val (x, y) = Resolution.detectNativeRes()
        return Resolution.values().firstOrNull { it.width == x && it.height == y } ?: Resolution._1920x1200
    }
}
