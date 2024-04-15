package org.yunghegel.salient.engine.api.dto

import kotlinx.serialization.Serializable
import org.yunghegel.salient.engine.api.dto.datatypes.CameraData


@Serializable
class SceneContextDTO {
    var cameraSettings : CameraData = CameraData()
    var sceneEnvironment: SceneEnvironmentDTO = SceneEnvironmentDTO()
}
