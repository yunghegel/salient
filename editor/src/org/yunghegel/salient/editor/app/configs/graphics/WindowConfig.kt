package org.yunghegel.salient.editor.app.configs.graphics

import com.badlogic.gdx.*
import org.yunghegel.salient.editor.app.configs.graphics.Resolution.*

import kotlinx.serialization.Serializable
import org.yunghegel.gdx.utils.reflection.Editable
import org.yunghegel.gdx.utils.reflection.Ignore
import org.yunghegel.salient.engine.ui.widgets.value.EditorFactory
import org.yunghegel.salient.engine.ui.widgets.value.widgets.EnumWidget
import org.yunghegel.salient.engine.events.lifecycle.onStartup
import kotlin.math.min

@Serializable
data class WindowConfig(
    @Editable(name = "Resolution Preset") var resolution_preset: Resolution = _1920x1080,
    @Editable(name = "Size X") var resolution_width_actual: Int = resolution_preset.width,
    @Editable(name = "Size Y") var resolution_height_actual: Int = resolution_preset.height,
    @Editable("Window X", readonly = true) var window_x: Int = -1,
    @Editable("Window Y", readonly = true) var window_y: Int = -1,
    @Editable(name = "Fullscreen") var fullscreen: Boolean = false,
    @Ignore var auto_iconify: Boolean = false,
    @Ignore var decorated: Boolean = true,
    @Ignore var resizable: Boolean = true,
    @Ignore var icon_path: String = "icon.png",
    @Editable("Title", readonly = true) var title: String = "Salient"
) {

    init {
        EditorFactory.register(Resolution::class.java) {
            EnumWidget(Resolution::class.java).create(it)
        }
        onStartup {
            resolution_preset = resolveResolution()
            resolution_width_actual = min(600f, Gdx.graphics.backBufferWidth.toFloat()).toInt()
            resolution_height_actual = min(400f, Gdx.graphics.backBufferHeight.toFloat()).toInt()
        }
    }

    fun resolveResolution(): Resolution {
        val (x, y) = Resolution.detectNativeRes()
        return Resolution.values().firstOrNull { it.width == x && it.height == y } ?: _1920x1200
    }
}
