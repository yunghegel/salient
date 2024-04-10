package org.yunghegel.salient.editor.app.configs.graphics

import org.yunghegel.salient.editor.app.configs.graphics.AntiAliasing.*
import kotlinx.serialization.Serializable
import org.yunghegel.salient.editor.app.configs.graphics.AntiAliasing

@Serializable
data class VideoConfig(
    var vsync: Boolean = false,
    var fps_cap: Int = 0,
    var idle_fps: Int = 0,
    var antiAliasing: AntiAliasing = _8
) {
}
