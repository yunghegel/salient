package org.yunghegel.salient.editor.app.configs.camera

import org.yunghegel.salient.editor.app.configs.camera.CameraConfiguration.*
import kotlinx.serialization.*

@Serializable
data class CameraParameters(
    var rotation_angle: Float = 360f,
    var translation_units: Float = 10f,
    var zoom_units: Float = 10f
) : CameraConfig()
