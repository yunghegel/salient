package org.yunghegel.salient.editor.project

import com.badlogic.gdx.files.FileHandle
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.editor.app.dto.DTOAdapter
import org.yunghegel.salient.editor.app.dto.ProjectDTO
import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.lifecycle.onShutdown
import org.yunghegel.salient.engine.events.scene.SceneChangedEvent
import org.yunghegel.salient.engine.io.Paths
import org.yunghegel.salient.engine.io.debug
import org.yunghegel.salient.engine.io.inject

typealias ProjectDirectory = FileHandle
typealias ProjectFile = FileHandle

class Project(handle: ProjectHandle,val manager:ProjectManager) : EditorProject<Project,Scene>(handle, manager) {

    val folder : ProjectDirectory = Paths.PROJECT_DIR_FOR(handle.name).handle

    val file : ProjectFile = Paths.PROJECT_FILE_FOR(handle.name).handle

    val sceneManager : SceneManager by lazy { inject() }

    override val scenes: MutableList<Scene> = mutableListOf()

    init {
        onShutdown {
            sceneIndex.forEach { handle ->
                val scene = scenes.find { it.handle == handle }
                println("matched handle to scene ${scene?.name}")
                SceneHandle.saveToFile(handle,this)
            }
        }
    }

    override var currentScene: Scene? = null
        set(value) {
            if (field == value) return
            require(value != null) { "Scene cannot be null" }
            val old = field
            field = value
            initalizeScene(value)
            post(SceneChangedEvent(old, value))
        }

    override fun initalizeScene(scene: Scene) {
        debug("Initializing scene ${scene.handle.name} for project ${handle.name}")
    }

    companion object Data : DTOAdapter<Project, ProjectDTO> {

        override fun fromDTO(dto: ProjectDTO) : Project{
            val proj = Project(dto.handle, inject())
            dto.assetRegistry.forEachIndexed {i,asset-> proj.assetIndex.add(i,asset) }
            dto.sceneRegistry.forEachIndexed { i,scene->proj.sceneIndex.add(i,scene) }
            return proj
        }

        override fun toDTO(model: Project): ProjectDTO {
            val dto =  ProjectDTO()
            dto.handle = model.handle
            dto.assetRegistry = Array(model.assetIndex.size) {model.assetIndex[it]}
            dto.sceneRegistry = Array(model.sceneIndex.size) {model.sceneIndex[it]}
            return dto
        }

    }

}