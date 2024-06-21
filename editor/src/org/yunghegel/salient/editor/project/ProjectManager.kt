package org.yunghegel.salient.editor.project

import com.charleskorn.kaml.Yaml
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.engine.api.Default
import org.yunghegel.salient.engine.api.EditorProjectManager
import org.yunghegel.salient.engine.api.dto.ProjectDTO
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.lifecycle.onShutdown
import org.yunghegel.salient.engine.events.project.ProjectChangedEvent
import org.yunghegel.salient.engine.events.project.ProjectCreatedEvent
import org.yunghegel.salient.engine.events.project.ProjectLoadedEvent
import org.yunghegel.salient.engine.events.project.ProjectSavedEvent
import org.yunghegel.salient.engine.helpers.save
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.file.Filepath.Companion.pathOf
import org.yunghegel.salient.engine.system.file.Paths
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.system.inject


class ProjectManager : EditorProjectManager<Project,Scene>, Default<Project> {

    private val sceneManager : SceneManager by lazy { inject() }
    private val assetManager : AssetManager by lazy { inject() }

    override var currentProject: Project? = null
        set(value) {
            require(value != null) { "Project cannot be null" }
            val old = field
            field = value
            post(ProjectChangedEvent(old, value))
        }
        get() {
            return field
        }

    init {
        onShutdown {
            saveProject(currentProject!!)
        }

    }

    override fun loadProject(file: Filepath): Project {
        val handle = ProjectHandle(file.name,file)
        val project = Project(handle,this)
        post(ProjectLoadedEvent(project))
        return project
    }

    override fun saveProject(project: Project) {
        val dto = Project.Data.toDTO(project)
        val path = project.file.pathOf()
        path.mkfile()
        val data = Yaml.default.encodeToString(ProjectDTO.serializer(),dto)
        info("Saving project ${project.handle.name}")
        save(path.path) { data }
        project.sceneIndex.forEach { handle ->
            SceneHandle.saveToFile(handle,project)
        }

        project.scenes.forEach { scene ->
            sceneManager.saveScene(scene)
        }
        post(ProjectSavedEvent(project))
    }

    override fun createNew(name: String): Project {
        createDirectories(name)
        val handle = ProjectHandle(name, Paths.PROJECT_DIR_FOR(name))
        val project = Project(handle,this)
        post(ProjectCreatedEvent(project))
        saveProject(project)
        return project
    }

    private fun createDirectories(name: String) {
        val projectDir = Paths.PROJECT_DIR_FOR(name)
        val sceneDir = Paths.SCENE_DIR_FOR(name)
        val assetDir = Paths.PROJECT_SCOPE_ASSETS_DIR_FOR(name)
        val projectAssets = Paths.PROJECT_ASSET_DIR_FOR(name)
        val sceneIndices = Paths.SCENE_INDEX_DIR_FOR(name)
        projectDir.mkdir()
        sceneDir.mkdir()
        assetDir.mkdir()
        sceneIndices.mkdir()
        projectAssets.mkdir()
    }


    override fun initialize(project: Project) {
        assetManager.initializeProject(project)
        currentProject = project
    }

    override fun createDefault(): Project {
        return createNew("default")
    }

}