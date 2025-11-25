package org.yunghegel.salient.editor.app

import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.project.ProjectManager
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.engine.api.ApplicationManager
import org.yunghegel.salient.engine.api.EditorProjectManager
import org.yunghegel.salient.engine.api.EditorSceneManager
import org.yunghegel.salient.engine.api.asset.EditorAssetManager
import org.yunghegel.salient.engine.system.file.Paths
import org.yunghegel.salient.engine.system.provide
import org.yunghegel.salient.engine.system.singleton

class Manager : ApplicationManager<Project,Scene,ProjectManager,SceneManager,AssetManager>(Paths.PROJECTS_DIR.handle) {

    override val assetManager: AssetManager = AssetManager()
    override val projectManager: ProjectManager = ProjectManager()
    override val sceneManager: SceneManager = SceneManager()

    init {
        singleton(assetManager)
        singleton(projectManager)
        singleton(sceneManager)

        provide {
            projectManager.currentProject ?: projectManager.createDefault().also { projectManager.initialize(it) }
        }
    }

        fun component1(): ProjectManager = projectManager
        fun component2(): SceneManager = sceneManager
        fun component3(): AssetManager = assetManager


}