package org.yunghegel.salient.editor.asset

import com.badlogic.gdx.files.FileHandle
import org.yunghegel.gdx.utils.data.serialize
import org.yunghegel.gdx.utils.ext.addIfNotPresent
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.gdx.utils.ext.withExtension
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.project.ProjectManager
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.asset.EditorAssetManager
import org.yunghegel.salient.engine.api.asset.type.*
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.scene.EditorScene.Companion.folder
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.asset.AssetIncludedEvent
import org.yunghegel.salient.engine.events.asset.AssetIndexedEvent
import org.yunghegel.salient.engine.helpers.from
import org.yunghegel.salient.engine.system.Log.info
import org.yunghegel.salient.engine.system.file.FileOps
import org.yunghegel.salient.engine.system.file.FileType
import org.yunghegel.salient.engine.system.file.Paths
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.system.profile
import org.yunghegel.salient.engine.system.warn

class AssetManager() : EditorAssetManager<Project, Scene> {


    override fun loadProjectIndex(project: Project) : List<AssetHandle> {
        val indices : MutableList<AssetHandle> = mutableListOf()
        info("Loading asset index for project: ${project.name}")
        val index = projectIndex(project)
        val valid = mutableListOf<FileHandle>()
        index.list.withExtension("asset").each { file->
            valid.add(file)
            info("Project contains indexed asset file: ${file.name()}")
        }
        valid.each { file ->
            deserializeHandle(file).onSuccess { asset ->
                info("Asset handle deserialized: $asset")
                indices.add(asset)
            }
        }
        return indices
    }

    override fun loadSceneIndex(scene: Scene, project: Project): List<AssetHandle> {
        val indices : MutableList<AssetHandle> = mutableListOf()
        info("Loading asset index for scene: ${scene.name}")
        val index = sceneIndex(project,scene)
        val valid = mutableListOf<FileHandle>()
        index.list.withExtension("uuid").each { file->
            valid.add(file)
            info("Scene contains indexed asset file: ${file.name()}")
        }
        valid.each { file ->
            val handle = project.assetIndex.find { it.uuid == file.nameWithoutExtension() }
            if (handle != null) {
                indices.add(handle)

            }
        }
        indices.each { handle ->
            scene.indexAsset(handle)
        }

        return indices
    }

    override fun deserializeHandle(file: FileHandle) : Result<AssetHandle> {
        info("Attempting to deserialize asset handle: ${file.name()}")
        val asset = try { AssetHandle::class.from(file.path()) } catch (e: Exception) {
            return Result.failure(e)
        }
        return Result.success(asset)
    }

    override fun preloadSceneAssets(scene: Scene,project: Project) {
        info("Preloading assets for scene: ${scene.name}")
        val handles = loadSceneIndex(scene,project)
        handles.forEach { handle -> includeAsset(handle,scene) }
    }

    override fun includeAsset(asset: AssetHandle, scene: Scene) {
        profile ("inlcude/load asset ${asset.name}") {
        info("Including asset for usage in scene: ${asset.name}")
        post(AssetIncludedEvent(asset,scene))
            profile("load_asset_${asset.name}") {
                val loadedAsset = loadAsset(asset)
                scene.assets.add(loadedAsset)
                scene.indexAsset(asset)
            }

        if(!scene.folder.containsChild(asset.path.handle)) {
            FileOps.addChildToDir(scene.folder.handle,asset.path.handle)
        }
        val manager : ProjectManager = inject()
        serializeHandle(asset,manager.currentProject!!,scene)
        }

    }

    override fun serializeHandle(asset: AssetHandle, project:Project) : Result<Unit> {
        info("Attempting to serialize asset handle: $asset")
        val file = projectIndex(project).child("${asset.name}.asset").handle
        file.file().createNewFile()

        val res = try { file.writeString(asset.serialize(),false) } catch (e: Exception) {
            return Result.failure(e)
        }
        return Result.success(res)
    }

    override fun serializeHandle(asset: AssetHandle, project: Project, scene: Scene): Result<Unit> {
        info("Attempting to serialize asset handle: $asset")
        val file = sceneIndex(project,scene).child("${asset.uuid}.uuid").handle
        file.file().createNewFile()

        val res = try { file.writeString(asset.uuid,false) } catch (e: Exception) {
            return Result.failure(e)
        }
        return Result.success(res)
    }

    override fun indexHandle(asset: AssetHandle, project: Project) {
        project.assetIndex.addIfNotPresent(asset) {
            warn("Attempting to index a handle which already exists.")
        }.takeIf { it }?.let {
            val res = serializeHandle(asset, project)
            res.onSuccess {
                info("Asset handle indexed: $asset")
                post(AssetIndexedEvent(asset))
            }
            res.onFailure {
                warn("Failed to index asset handle: $asset")
            }
        }
    }

    override fun loadAsset(asset: AssetHandle): Asset<*> {
        info("Attempting to load asset: ${asset.name}")
        val filetype = FileType.parse(asset.path.extension)
        val type = AssetType.fromFiletype(filetype)

            when (type) {
                AssetType.Model -> {
                    val model = ModelAsset(asset.path,asset,asset)
                    model.load()
                    return model
                }
                AssetType.Texture -> {
                    val texture = TextureAsset(asset.path)
                    texture.load()
                    return texture
                }
                AssetType.Shader -> {
                    val shader = ShaderAsset(asset.path,asset.name)
                    shader.load()
                    return shader
                }
                AssetType.Material -> {
                    val material = MaterialAsset(asset.path)
                    material.load()
                    return material
                }
                else -> {
                    warn("Asset type not recognized: $type")
                    val file = FileAsset(asset.path)
                    file.load()
                    return file
                }
            }


    }

    override fun createHandle(file: FileHandle): AssetHandle {
        return AssetHandle(file.path())
    }

    fun exportSceneIndex(scene: Scene, project: Project) {
        val index = sceneIndex(project,scene)
        val children = index.list.map { it.name() }
        scene.retrieveAssetIndex().forEach {
            if (it.name !in children) {
                val file = index.child("${it.uuid}.uuid").handle
                file.file().createNewFile()
                file.writeString(it.uuid,false)
            }
        }
    }



    companion object {
        val projectIndex = { proj: Project -> Paths.PROJECT_SCOPE_ASSETS_DIR_FOR(proj.name) }
        val sceneIndex = { proj: Project, scene: Scene -> Paths.SCENE_SCOPE_ASSET_INDEX_DIR_FOR(proj.name, scene.name) }
    }

}