package org.yunghegel.salient.engine.api

import com.badlogic.gdx.files.FileHandle
import com.charleskorn.kaml.Yaml
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.engine.api.asset.EditorAssetManager
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.system.file.file
import kotlin.properties.Delegates

abstract class ApplicationManager<T: EditorProject<T,S>,S:EditorScene, PManager: EditorProjectManager<T,S>, SManager: EditorSceneManager<S>, AManager: EditorAssetManager<T,S>>(val root: FileHandle) {


    abstract val projectManager: PManager

    abstract val sceneManager: SManager

    abstract val assetManager: AManager

    var meta : Meta by Delegates.notNull()

    val fileRoot by file(root)

    lateinit var project : T

    val indices : MutableList<ProjectHandle> = mutableListOf()

    val projectsFolder : FileHandle = fileRoot.child("projects")

    init {

    }

    fun bootstrapProject(rootDirectory: FileHandle) : T {
        meta = rootDirectory.getChild(".salient.meta") { meta ->
            Yaml.default.decodeFromString(Meta.serializer(), meta.readString())
        } ?: Meta()
        val projectHandle : ProjectHandle = meta.lastLoadedProject ?: projectManager.createHandle("default")
        val proj = if (!projectHandle.exists) projectManager.createNew("default")
        else projectManager.loadProject(projectHandle.file)
        return proj
    }

    fun loadIndicies(projectsFolder : FileHandle) {
        projectsFolder.child(".project-index").ls { file ->
            println("loading index: $file")
            val proj : ProjectHandle = Yaml.default.decodeFromString(ProjectHandle.serializer(), file.readString())
            indices.add(proj)
        }
    }

    fun saveIndicies(projectsFolder : FileHandle) {
        val index = projectsFolder.dir(".project-index")
        indices.forEach { proj ->
            println("indexing project: $proj")
            index.touch(proj.uuid) { file ->
                file.writeString(Yaml.default.encodeToString(ProjectHandle.serializer(), proj),false)
            }
        }
    }

    fun populateProject(project : T) {
        val latest = meta.lastLoadedScene ?: project.sceneIndex.lastOrNull() ?: sceneManager.createHandle("default",project)
        println("loading scene: $latest")
        val scene  = if (!latest.exists) sceneManager.createNew("default")
        else sceneManager.loadScene(latest.file, true)
        project.initalizeScene(scene)
        assetManager.loadSceneIndex(scene,project).forEach { handle ->
            println("loading asset: $handle")
            assetManager.loadAsset(handle)
        }
    }






}