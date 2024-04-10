package org.yunghegel.salient.modules.input.shared.config

import org.yunghegel.salient.modules.input.shared.config.CameraConfiguration.CameraConfig
import kotlinx.serialization.*
import org.yunghegel.salient.core.input.view.*
import org.yunghegel.salient.core.input.view.CameraMode.*
import org.yunghegel.salient.core.input.view.NavigationMethod.*

@Serializable
data class CameraModes(
    var camera_mode: CameraMode = PERSPECTIVE,
    var camera_controls: NavigationMethod = BLENDER_CAMEARA
) : CameraConfig()
