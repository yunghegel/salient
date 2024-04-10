package org.yunghegel.salient.editor.app.dto

import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.api.scene.EditorScene

class SceneDTO : DTO<Scene?>(EditorScene::class.java.name) {
    var handle: SceneHandle? = null

    var assetUsage: Array<AssetHandle>

    var sceneGraph: SceneGraphDTO = SceneGraphDTO()

    var sceneContext: SceneContextDTO = SceneContextDTO()
}
