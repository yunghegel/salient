package org.yunghegel.salient.editor.app.configs.graphics

import com.badlogic.gdx.*
import org.yunghegel.salient.editor.app.configs.graphics.Resolution.*

import kotlinx.serialization.Serializable
import org.yunghegel.salient.engine.events.lifecycle.onStartup

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
