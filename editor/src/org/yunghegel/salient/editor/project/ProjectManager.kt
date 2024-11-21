package org.yunghegel.salient.editor.project

import com.badlogic.gdx.files.FileHandle
import com.charleskorn.kaml.Yaml
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.engine.*
import org.yunghegel.salient.engine.api.EditorProjectManager
import org.yunghegel.salient.engine.api.Meta
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
import org.yunghegel.salient.engine.system.debug
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.file.Filepath.Companion.pathOf
import org.yunghegel.salient.engine.system.file.Paths
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.system.inject


class ProjectManager : EditorProjectManager<Project,Scene>() {

    private val sceneManager : SceneManager by lazy { inject() }
    private val assetManager : AssetManager by lazy { inject() }

    override var projectDir: FileHandle = Paths.PROJECTS_DIR.handle

    val meta : Meta by lazy { inject() }

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

    override fun loadProject(file: Filepath): Project {
        state = LOAD_PROJECT
        val handle = createHandle(file.handle.nameWithoutExtension())
        val data = file.readString
        val dto : ProjectDTO = Yaml.default.decodeFromString(ProjectDTO.serializer(),data)
        val project = Project.Data.fromDTO(dto)
        debug("Loading project\n${project.toString()}")

        post(ProjectLoadedEvent(project))

        if (currentProject==null) {
            currentProject = project
        }

        return project
    }

    override fun saveProject(project: Project) {
        val dto = Project.Data.toDTO(project)
        val path = project.filepath.pathOf()
        path.mkfile()
        val data = Yaml.default.encodeToString(ProjectDTO.serializer(),dto)
        info("Saving project ${project.handle.name}")
        save(path.path) { data }
        project.sceneIndex.forEach { handle ->
            SceneHandle.saveToFile(handle,project)
        }

        assetManager.exportProjectIndex(project)

        project.scenes.forEach { scene ->
            sceneManager.saveScene(scene)
        }
        post(ProjectSavedEvent(project))
    }

    override fun createNew(name: String): Project {
        createDirectories(name)
        val handle = ProjectHandle(name, Paths.PROJECT_FILE_FOR(name))
        val project = Project(handle)
        if (currentProject == null) {
            currentProject = project
        }
        post(ProjectCreatedEvent(project))
        return project
    }

    private fun createDirectories(name: String) {
        val projectDir = Paths.PROJECT_DIR_FOR(name)
        val sceneDir = Paths.SCENE_DIR_FOR(name)
        val assetDir = Paths.PROJECT_ASSET_INDEX_FOR(name)
        val projectAssets = Paths.PROJECT_ASSET_DIR_FOR(name)
        val sceneIndices = Paths.SCENE_INDEX_DIR_FOR(name)
        projectDir.mkdir()
        sceneDir.mkdir()
        assetDir.mkdir()
        sceneIndices.mkdir()
        projectAssets.mkdir()
    }


    override fun initialize(project: Project) {
        state = INITIALIZE_PROJECT doing {
            assetManager.initializeProject(project)
            currentProject = project
        }

    }

    override fun createDefault(): Project {
        val proj =  createNew("default")
//        assetManager.queueAssetLoad(AssetHandle("models/gltf/logo.gltf"))
        return proj
    }

}