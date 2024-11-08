package org.yunghegel.salient.editor.scene

import com.badlogic.gdx.Gdx
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import org.yunghegel.gdx.utils.ext.addIfNotPresent
import org.yunghegel.salient.editor.asset.AssetManager
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.project.ProjectManager
import org.yunghegel.salient.engine.api.Default
import org.yunghegel.salient.engine.api.EditorSceneManager
import org.yunghegel.salient.engine.api.asset.type.ModelAsset
import org.yunghegel.salient.engine.api.dto.SceneDTO
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.scene.SceneLoadedEvent
import org.yunghegel.salient.engine.events.scene.SceneSavedEvent
import org.yunghegel.salient.engine.helpers.Ignore
import org.yunghegel.salient.engine.helpers.save
import org.yunghegel.salient.engine.system.*
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.file.Paths

class SceneManager : EditorSceneManager<Scene>(), Default<Scene>{

    val projectManager : ProjectManager by lazy { inject() }

    val assetManager : AssetManager by lazy { inject() }


    val project: Project by lazy { projectManager.currentProject ?: inject() }

    init {
        try { InjectionContext.setProvider(SceneGraph::class.java) { project.currentScene!!.graph } } catch (e:Exception) { }


    }

    override fun createNew(name: String): Scene {
        require(projectManager.currentProject != null)

        val path  = Paths.SCENE_FILE_FOR(projectManager.currentProject?.name!!,name)
        path.parent.mkdir()


        val handle = SceneHandle(name,path)
        val scene = Scene(handle,projectManager.currentProject!!)
        makeDirectories(projectManager.currentProject!!,scene)
        val handlepath = Paths.SCENE_INDEX_FILEPATH_FOR(projectManager.currentProject?.name!!,name)
        if (!handlepath.exists) {
            SceneHandle.saveToFile(handle,projectManager.currentProject!!)
        }
        saveScene(scene)
        return scene
    }

    override fun loadScene(file: Filepath, makeCurrent: Boolean): Scene {

            val path = Paths.SCENE_FILE_FOR(projectManager.currentProject?.name!!, file.handle.nameWithoutExtension())
            val handle = projectManager.currentProject?.sceneIndex?.find { it.path == path }
                ?: projectManager.currentProject?.scenes!!.find { it.handle.name == file.name }?.let { it.handle }
                ?: SceneHandle(file.name, path)

            val data = handle.path.readString
            val dto = Yaml.default.decodeFromString(SceneDTO.serializer(), data)

            val scene: Scene = Scene.fromDTO(dto)

            post(SceneLoadedEvent(scene))
            if (makeCurrent) {
                initialize(scene, makeCurrent = true)
            }

            project.sceneIndex.addIfNotPresent(handle) {
                warn("Scene index already contains handle: ${handle.name}")
            }

        return scene
    }

    private fun makeDirectories(project:Project, scene:Scene) {
        Paths.SCENE_DIR_FOR(project.name).mkdir()
        Paths.SCENE_SCOPE_ASSET_INDEX_DIR_FOR(project.name,scene.handle.name).mkdir()
//        Paths.SCENE_SCOPE_ASSET_DIR_FOR(project.name,scene.handle.name).mkdir()
    }

    override fun initialize(scene: Scene,makeCurrent:Boolean) {
        projectManager.currentProject?.scenes?.add(scene)
//        assetManager.initializeScene(scene,projectManager.currentProject!!)
        if(makeCurrent) projectManager.currentProject?.currentScene = scene
//        scene.retrieveAssetIndex().forEach { assetHandle ->
//            assetManager.includeAsset(assetHandle,scene)
//        }
        debug("Scene Initialized: ${scene.handle.name}")
    }

    override fun saveScene(scene: Scene) {
        val path = scene.handle.path
        val handle = scene.handle
        val proj = projectManager.currentProject!!
        val dto = Scene.Data.toDTO(scene)
        val data = Yaml.default.encodeToString(SceneDTO.serializer(),dto)

        handle.proj?.indexScene(handle)
        path.mkfile()
        info("Saving scene ${scene.handle.name} to ${path.path}")
        SceneHandle.saveToFile(handle,proj)
        assetManager.exportSceneIndex(scene,proj)
        save(path.path) { data }
        post(SceneSavedEvent(scene))
    }


    override fun createDefault(): Scene {
        val default =  createNew("default")
        val defaultAssetHandle = assetManager.createHandle(Gdx.files.internal("models/gltf/logo.gltf"))
        assetManager.indexHandle(defaultAssetHandle,project)
        assetManager.includeAsset(defaultAssetHandle,default)
        default.findAsset(defaultAssetHandle)?.let { asset ->
            asset as ModelAsset

        }

        return default
    }

}