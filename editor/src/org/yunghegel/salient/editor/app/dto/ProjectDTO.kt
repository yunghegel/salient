package org.yunghegel.salient.editor.app.dto

import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.api.project.EditorProject

class ProjectDTO : DTO<Project?>(EditorProject::class.java.name) {
    var handle: ProjectHandle? = null

    var sceneRegistry: Array<SceneHandle>

    var assetRegistry: Array<AssetHandle>
}
