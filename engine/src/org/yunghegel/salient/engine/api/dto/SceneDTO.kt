package org.yunghegel.salient.engine.api.dto

import kotlinx.serialization.Serializable
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.SceneHandle

@Serializable
class SceneDTO {
    var handle: SceneHandle? = null

    var assetIndex: MutableList<AssetHandle> = mutableListOf()

    var sceneGraph: SceneGraphDTO = SceneGraphDTO()

    var sceneContext: SceneContextDTO = SceneContextDTO()
}
