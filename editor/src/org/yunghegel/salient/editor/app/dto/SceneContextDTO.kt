package org.yunghegel.salient.editor.app.dto

import kotlinx.serialization.Serializable
import org.yunghegel.salient.editor.app.dto.datatypes.CameraData


@Serializable
class SceneContextDTO {
    var cameraSettings : CameraData = CameraData()
    var sceneEnvironment: SceneEnvironmentDTO = SceneEnvironmentDTO()
}
