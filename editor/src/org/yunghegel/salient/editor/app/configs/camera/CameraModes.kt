package org.yunghegel.salient.editor.app.configs.camera

import org.yunghegel.salient.editor.app.configs.camera.CameraConfiguration.CameraConfig
import kotlinx.serialization.*
import org.yunghegel.salient.editor.app.configs.camera.CameraMode.*
import org.yunghegel.salient.editor.app.configs.camera.NavigationMethod.*

@Serializable
data class CameraModes(
    var camera_mode: CameraMode = PERSPECTIVE,
    var camera_controls: NavigationMethod = BLENDER_CAMEARA
) : CameraConfig()
