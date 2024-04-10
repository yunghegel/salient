package org.yunghegel.salient.modules.input.shared.config

import org.yunghegel.salient.modules.input.shared.config.CameraConfiguration.*
import kotlinx.serialization.*

@Serializable
data class CameraKeybinds(
    var tool: Int = 0, var rotate: Int = 1, var pan: Int = 2, var zoom: Int = 3,
    var reset: String = "R", var focus: String = ".", var home: String = "Home"
) : CameraConfig()
