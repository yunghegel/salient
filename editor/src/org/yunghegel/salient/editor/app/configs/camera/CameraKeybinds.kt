package org.yunghegel.salient.editor.app.configs.camera

import org.yunghegel.salient.editor.app.configs.camera.CameraConfiguration.*
import kotlinx.serialization.*

@Serializable
data class CameraKeybinds(
    var tool: Int = 0, var rotate: Int = 1, var pan: Int = 2, var zoom: Int = 3,
    var reset: String = "R", var focus: String = ".", var home: String = "Home"
) : CameraConfig()
