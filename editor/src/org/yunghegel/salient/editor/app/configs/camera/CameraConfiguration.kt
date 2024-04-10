package org.yunghegel.salient.editor.app.configs.camera

import kotlinx.serialization.*
import org.yunghegel.salient.editor.app.configs.camera.CameraKeybinds
import org.yunghegel.salient.editor.app.configs.camera.CameraModes
import org.yunghegel.salient.editor.app.configs.camera.CameraParameters

@Serializable
data class CameraConfiguration(
    val keybinds: CameraKeybinds = CameraKeybinds(),
    var camera_modes: CameraModes = CameraModes(),
    var parameters: CameraParameters = CameraParameters()
) {

    sealed class CameraConfig

}
