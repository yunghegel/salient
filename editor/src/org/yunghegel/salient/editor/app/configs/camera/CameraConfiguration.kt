package org.yunghegel.salient.editor.app.configs.camera

import com.badlogic.gdx.graphics.Camera
import kotlinx.serialization.*
import org.yunghegel.gdx.utils.ext.ref
import org.yunghegel.salient.engine.api.Configuration
import org.yunghegel.salient.engine.api.dto.datatypes.CameraData
import org.yunghegel.salient.engine.system.inject

@Serializable
data class CameraConfiguration(
    var keybinds: CameraKeybinds = CameraKeybinds(),
    var camera_modes: CameraModes = CameraModes(),
    var parameters: CameraParameters = CameraParameters(),
) : Configuration() {

    sealed class CameraConfig

}
