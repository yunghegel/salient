package org.yunghegel.salient.editor.app.configs.graphics

import kotlinx.serialization.Serializable
import org.yunghegel.salient.editor.app.configs.graphics.AntiAliasing._8
import org.yunghegel.salient.engine.ui.widgets.value.EditorFactory
import org.yunghegel.salient.engine.ui.widgets.value.widgets.EnumWidget

@Serializable
data class VideoConfig(

    var vsync: Boolean = false,
    var fps_cap: Int = 0,
    var idle_fps: Int = 0,
    var antiAliasing: AntiAliasing = _8

) {
    init {
        EditorFactory.register(AntiAliasing::class.java) {
           EnumWidget(AntiAliasing::class.java).create(it)
        }
    }
}
