package org.yunghegel.salient.modules.graphics.shared.config

import org.yunghegel.salient.modules.graphics.shared.config.AntiAliasing.*
import kotlinx.serialization.Serializable

@Serializable
data class VideoConfig(
    var vsync: Boolean = false,
    var fps_cap: Int = 0,
    var idle_fps: Int = 0,
    var antiAliasing: AntiAliasing = _8
) {
}
