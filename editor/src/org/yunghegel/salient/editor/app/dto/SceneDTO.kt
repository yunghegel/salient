package org.yunghegel.salient.editor.app.dto

import kotlinx.serialization.Serializable
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.SceneHandle

@Serializable
class SceneDTO()  {
    var handle: SceneHandle? = null

    var assetUsage: Array<AssetHandle> = arrayOf()

    var sceneGraph: SceneGraphDTO = SceneGraphDTO()

    var sceneContext: SceneContextDTO = SceneContextDTO()
}
