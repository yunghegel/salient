package org.yunghegel.salient.engine.api.dto

import kotlinx.serialization.Serializable
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.system.file.Filepath

@Serializable
class ProjectDTO {
    var handle: ProjectHandle = ProjectHandle("null", Filepath("null"))

    var sceneRegistry: Array<SceneHandle> = arrayOf()

    var assetRegistry: Array<AssetHandle> = arrayOf()
}
