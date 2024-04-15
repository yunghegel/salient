package org.yunghegel.salient.engine.api.dto

import kotlinx.serialization.Serializable

@Serializable
class SceneGraphDTO {
    var root: GameObjectDTO = GameObjectDTO()
}
