package org.yunghegel.salient.engine.api.dto

import kotlinx.serialization.Serializable
import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.SceneHandle

@Serializable
class SceneDTO {
    var handle: SceneHandle? = null

    var asset_index: MutableList<AssetHandle> = mutableListOf()
    var scene_materials  : MutableList<ID> = mutableListOf()
    var scene_graph: SceneGraphDTO = SceneGraphDTO()
    var scene_context: SceneContextDTO = SceneContextDTO()




}
