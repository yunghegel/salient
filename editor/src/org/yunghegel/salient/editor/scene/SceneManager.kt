package org.yunghegel.salient.editor.scene

import com.charleskorn.kaml.Yaml
import org.yunghegel.salient.editor.app.dto.SceneDTO
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.project.ProjectManager
import org.yunghegel.salient.engine.api.Default
import org.yunghegel.salient.engine.api.EditorSceneManager
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.scene.SceneLoadedEvent
import org.yunghegel.salient.engine.events.scene.SceneSavedEvent
import org.yunghegel.salient.engine.helpers.save
import org.yunghegel.salient.engine.io.*


class SceneManager : EditorSceneManager<Scene>, Default<Scene>{

    val projectManager : ProjectManager by lazy { inject() }

    val project: Project by lazy { projectManager.currentProject ?: inject() }

    init {
        provide<SceneGraph> { project.currentScene!!.sceneGraph }
    }

    override fun createNew(name: String): Scene {
        require(projectManager.currentProject != null)
        val path  = Paths.SCENE_DIR_FOR(projectManager.currentProject?.name!!).child("$name.scene")
        val handle = SceneHandle(name,path)
        val scene = Scene(handle,projectManager.currentProject!!,this)
        val handlepath = Paths.SCENE_INDEX_FILEPATH_FOR(projectManager.currentProject?.name!!,name)
        if (!handlepath.exists) {
            SceneHandle.saveToFile(handle,projectManager.currentProject!!)
        }
        saveScene(scene)
        return scene
    }

    override fun loadScene(file: Filepath,makeCurrent: Boolean): Scene {
        val path = Paths.SCENE_DIR_FOR(projectManager.currentProject?.name!!).child("${file.name}.scene")
        val handle = projectManager.currentProject?.sceneIndex?.find { it.path == path } ?: projectManager.currentProject?.scenes!!.find { it.handle.name == file.name }?.let {it.handle} ?: SceneHandle(file.name,path)

        val data = handle.path.readString
        val dto = Yaml.default.decodeFromString(SceneDTO.serializer(),data)
        println(dto)
        val scene: Scene =  Scene.fromDTO(dto)
        post(SceneLoadedEvent(scene))
        if (makeCurrent) {
            initialize(scene,makeCurrent = true)
        }
        return scene
    }

    override fun initialize(scene: Scene,makeCurrent:Boolean) {
        projectManager.currentProject?.scenes?.add(scene)
        if(makeCurrent) projectManager.currentProject?.currentScene = scene
        debug("Scene Initialized: ${scene.handle.name}")
    }

    override fun saveScene(scene: Scene) {
        val dto = Scene.Data.toDTO(scene)
        val data = Yaml.default.encodeToString(SceneDTO.serializer(),dto)
        println("SERIALIZING SCENE: \n$data")
        val path = scene.handle.path
        val handle = scene.handle
        handle.proj?.indexScene(handle)
        path.mkfile()
        SceneHandle.saveToFile(handle,projectManager.currentProject!!)
        save(path.path) { data }
        post(SceneSavedEvent(scene))
    }

    fun returnOrCreateIndex(sceneName: String, projectName:String) : SceneHandle {
        val scenePath = Paths.SCENE_FILE_FOR(projectName,sceneName)
        val handlePath = Paths.SCENE_INDEX_FILEPATH_FOR(projectName,sceneName)
        return if (handlePath.exists) {
            SceneHandle.loadFromFile(handlePath,projectManager.currentProject ?: inject())
        } else {
            SceneHandle(sceneName,scenePath)
        }
    }

    override fun createDefault(): Scene {
        return createNew("default")
    }

}