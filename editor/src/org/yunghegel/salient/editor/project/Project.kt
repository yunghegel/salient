package org.yunghegel.salient.editor.project

import com.badlogic.gdx.files.FileHandle
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.yunghegel.gdx.utils.reflection.Ignore
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.api.EditorProjectManager
import org.yunghegel.salient.engine.api.dto.DTOAdapter
import org.yunghegel.salient.engine.api.dto.ProjectDTO
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.api.properties.NamedObjectResource
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.lifecycle.onShutdown
import org.yunghegel.salient.engine.events.scene.SceneChangedEvent
import org.yunghegel.salient.engine.helpers.SFile
import org.yunghegel.salient.engine.system.debug
import org.yunghegel.salient.engine.system.file.Paths
import org.yunghegel.salient.engine.system.inject

typealias ProjectDirectory = SFile
typealias ProjectFile = SFile


class Project(val project_handle: ProjectHandle) : EditorProject<Project,Scene>(project_handle), NamedObjectResource by project_handle {


    val file : ProjectFile = Paths.PROJECT_FILE_FOR(handle.name).handle

    @Transient
    override val projectManager: ProjectManager = inject()



    init {
        onShutdown {
            sceneIndex.forEach { handle ->
                val scene = scenes.find { it.handle == handle }
                SceneHandle.saveToFile(handle,this)
            }
        }
    }

    @Transient
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
        if (!sceneIndex.contains(scene.handle)) {
            sceneIndex.add(scene.handle)
        }
    }

    override fun toString(): String {
        return "Project: [$name, $handle]\nScenes: ${sceneIndex.joinToString()}\nAssets: ${assetIndex.joinToString()}"
    }


    companion object Data : DTOAdapter<Project, ProjectDTO> {

        override fun fromDTO(dto: ProjectDTO) : Project{
            val proj = Project(dto.handle)
            dto.assetRegistry.forEachIndexed {i,asset-> proj.assetIndex.add(i,asset) }
            dto.sceneRegistry.forEachIndexed { i,scene->proj.sceneIndex.add(i,scene) }
            return proj
        }

        override fun toDTO(model: Project): ProjectDTO {
            val dto =  ProjectDTO()
            dto.handle = model.handle
            dto.assetRegistry = model.assetIndex.toTypedArray()
            dto.sceneRegistry = model.sceneIndex.toTypedArray()
            println(dto.sceneRegistry.joinToString())

            return dto
        }

    }

}