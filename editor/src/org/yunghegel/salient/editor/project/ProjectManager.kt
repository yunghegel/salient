package org.yunghegel.salient.editor.project

import com.charleskorn.kaml.Yaml
import org.yunghegel.salient.editor.app.dto.ProjectDTO
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.engine.api.Default
import org.yunghegel.salient.engine.api.EditorProjectManager
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.lifecycle.onShutdown
import org.yunghegel.salient.engine.events.project.ProjectChangedEvent
import org.yunghegel.salient.engine.events.project.ProjectLoadedEvent
import org.yunghegel.salient.engine.events.project.ProjectSavedEvent
import org.yunghegel.salient.engine.helpers.save
import org.yunghegel.salient.engine.io.Filepath
import org.yunghegel.salient.engine.io.Filepath.Companion.pathOf
import org.yunghegel.salient.engine.io.Paths
import org.yunghegel.salient.engine.io.inject


class ProjectManager : EditorProjectManager<Project,Scene>, Default<Project> {

    val sceneManager : SceneManager by lazy { inject() }

    override var currentProject: Project? = null
        set(value) {
            require(value != null) { "Project cannot be null" }
            val old = field
            field = value
            post(ProjectChangedEvent(old, value))
        }

    init {
        onShutdown {
            saveProject(currentProject!!)
        }

    }

    override fun loadProject(file: Filepath, setCurrent:Boolean): Project {
        val handle = ProjectHandle(file.name,file)
        val project = Project(handle,this)
        currentProject = project
        post(ProjectLoadedEvent(project))
        if (setCurrent) initialize(project)
        return project
    }

    override fun saveProject(project: Project) {
        val dto = Project.Data.toDTO(project)
        val path = project.file.pathOf()
        path.mkfile()
        val data = Yaml.default.encodeToString(ProjectDTO.serializer(),dto)
        println("SERIALIZING PROJECT: \n$data")
        println(project.handle.path)
        save(path.path) { data }
        project.sceneIndex.forEach { handle ->
            SceneHandle.saveToFile(handle,project)
        }

        project.scenes.forEach { scene ->
            sceneManager.saveScene(scene)
        }
        post(ProjectSavedEvent(project))
    }

    override fun createNew(name: String): ProjectHandle {
        createDirectories(name)
        val handle = ProjectHandle(name, Paths.PROJECT_DIR_FOR(name))
        val project = Project(handle,this)
        currentProject = project
        post(ProjectLoadedEvent(project))
        saveProject(project)
        return handle
    }

    fun createDirectories(name: String) {
        val projectDir = Paths.PROJECT_DIR_FOR(name)
        val sceneDir = Paths.SCENE_DIR_FOR(name)
        val assetDir = Paths.PROJECT_SCOPE_ASSETS_DIR_FOR(name)
        val sceneIndices = Paths.SCENE_INDEX_DIR_FOR(name)
        projectDir.mkdir()
        sceneDir.mkdir()
        assetDir.mkdir()
        sceneIndices.mkdir()
    }

    fun indexProject(project: Project) {
        val handle = project.handle
    }

    override fun initialize(project: Project) {
        currentProject = project
    }

    override fun createDefault(): Project {
        createNew("default")
        return currentProject!!
    }

}