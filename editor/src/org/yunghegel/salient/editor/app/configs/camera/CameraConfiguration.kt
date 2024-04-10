package org.yunghegel.salient.modules.input.shared.config

import kotlinx.serialization.*
import kotlinx.serialization.modules.*

@Serializable
data class CameraConfiguration(
    val keybinds: CameraKeybinds = CameraKeybinds(),
    var camera_modes: CameraModes = CameraModes(),
    var parameters: CameraParameters = CameraParameters()
) {

    sealed class CameraConfig

}
