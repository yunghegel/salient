package org.yunghegel.salient.editor.asset

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ktx.assets.async.AssetStorage
import ktx.async.KtxAsync
import ktx.async.onRenderingThread
import ktx.collections.GdxArray
import net.mgsx.gltf.loaders.glb.GLBAssetLoader
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader
import net.mgsx.gltf.scene3d.scene.SceneAsset
import org.yunghegel.gdx.utils.data.serialize
import org.yunghegel.gdx.utils.ext.addIfNotPresent
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.gdx.utils.ext.withExtension
import org.yunghegel.salient.editor.project.Project
import org.yunghegel.salient.editor.project.ProjectManager
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.*
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.asset.EditorAssetManager
import org.yunghegel.salient.engine.api.asset.Extras
import org.yunghegel.salient.engine.api.asset.type.*
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.scene.EditorScene.Companion.folder
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.asset.AssetIncludedEvent
import org.yunghegel.salient.engine.events.asset.AssetIndexedEvent
import org.yunghegel.salient.engine.helpers.encodestring
import org.yunghegel.salient.engine.helpers.from
import org.yunghegel.salient.engine.system.*
import org.yunghegel.salient.engine.system.Log.info
import org.yunghegel.salient.engine.system.file.FileOps
import org.yunghegel.salient.engine.system.file.FileType
import org.yunghegel.salient.engine.system.file.Paths
import java.util.*

private typealias GdxAssetManager = com.badlogic.gdx.assets.AssetManager

@Suppress("MoveLambdaOutsideParentheses")
class AssetManager() : EditorAssetManager<Project, Scene> {

    val storage = AssetStorage()

    val assetRegistry : EnumMap<AssetType,GdxArray<Asset<*>>> by lazy { EnumMap<AssetType,GdxArray<Asset<*>>>(AssetType::class.java) }

    init {
        setLoaders()
        debug("ASSET MANAGER INITIALIZED")

        AssetType.entries.each { type ->
            assetRegistry[type] = GdxArray()
        }

        // Only register AssetStorage if not already registered
        if (!InjectionContext.contains(AssetStorage::class)) {
            singleton(storage)
        }
    }

    private fun setLoaders() {
        storage.apply {
            setLoader(SceneAsset::class.java,"gltf",{ GLTFAssetLoader()})
            setLoader(SceneAsset::class.java,"glb", { GLBAssetLoader() })
            setLoader(Material::class.java,"material", { MaterialLoader() })
        }

    }

    val queue : Stack<Deferred<Any>> = Stack()

    override fun queueAssetLoad(asset: AssetHandle) : Deferred<Any> {
        val ext = asset.file.extension
        val filetype = FileType.parse(ext)
        val type = AssetType.fromFiletype(filetype)
        return run { KtxAsync.async(storage.asyncContext) {

            with(storage) {
                when (type) {
                    AssetType.Model -> {
                        val asset = ModelAsset(asset.file,asset,asset)
                        asset.load()
                        asset
                    }
                    AssetType.Texture -> load<Texture>(asset.file.toString())
                    AssetType.Shader -> load<ShaderProgram>(asset.file.toString())
                    AssetType.Material -> load<Material>(asset.file.toString())
                    AssetType.Other -> load<FileHandle>(asset.file.toString())
                }
            }
        } }
    }

    fun initializeProject(proj: Project) {
//        val projIndex = loadProjectIndex(proj)
//        projIndex.forEach { handle -> proj.indexAsset(handle) }
        info("Initializing project assets for ${proj.name}")

        proj.assetIndex.forEach { handle ->
//            require(proj.assetIndex.contains(handle)) { "Asset not found in project index" }
            indexHandle(handle,proj)
        }
//        proj.scenes.each { scene ->
//            val indices = loadSceneIndex(scene,proj)
//            indices.forEach { handle ->
//                require(proj.assetIndex.contains(handle) && scene.index.contains(handle)) { "Asset not found in project index or scene index" }
//
//            }
//        }
    }

    fun initializeScene(scene: Scene,project: Project) {
        info("Initializing scene assets for ${scene.ref.name}")
        state = INITIALIZE_SCENE
            val indexed = loadSceneIndex(scene,project)
            debug("scene index contains ${scene.index.size} assets")

            info("ensuring that an asset folder exists for this scene")

            if (!scene.ref.file.exists) {
                scene.folder.mkdir()
            }

            scene.index.forEach { handle ->
                require(project.assetIndex.contains(handle)) { "Asset not found in project index" }
                includeAsset(handle,scene)
            }
            true

//        first, file discovery; this reads the filesystem and includes them in the scene object's asset usage

    }


    override fun loadProjectIndex(project: Project) : List<AssetHandle> {
        val indices : MutableList<AssetHandle> = mutableListOf()
        state = PROJECT_ASSET_INDEX_DISCOVERY doing {
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
        }

        return indices
    }

    fun exportProjectIndex(project: Project) {
        val index = projectIndex(project)
        val children = index.list.map { it.name() }
        project.assetIndex.forEach {
            if (it.name !in children) {
                val file = index.child("${it.name}.asset").handle
                file.file().createNewFile()
                file.writeString(it.serialize(),false)
            }
        }
    }

    override fun loadSceneIndex(scene: Scene, project: Project): List<AssetHandle> {
        val indices : MutableList<AssetHandle> = mutableListOf()
        state = SCENE_ASSET_INDEX_DISCOVERY doing {
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
            debug("found ${indices.size} assets in scene ")
            profile("sync load") {

                indices.each { handle ->
                    scene.indexAsset(handle)
                }
            }
        }

//        profile("aysnc load") {
//            val deferred = indices.map { handle ->
//                val awaited = queueAssetLoad(handle)
//                awaited.invokeOnCompletion { println("Finished loading $handle") }
//                awaited
//            }
//            KtxAsync.launch {
//                deferred.awaitAll()
//                println("All assets loaded")
//            }
//
//        }

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

    override fun includeAsset(asset: AssetHandle, scene: Scene) : Asset<*> {
        var loadedAsset: Asset<*>? = locateAsset(asset)
        profile ("inlcude/load asset ${asset.name}") {
        info("Including asset for usage in scene: ${asset.name}")
        if (loadedAsset != null) {
            info("Asset already loaded: ${asset.name}")
            scene.assets.add(loadedAsset!!)
            scene.indexAsset(asset)
        } else {
            profile("load_asset_${asset.name}") {


                loadedAsset = loadAsset(asset)
                scene.assets.add(loadedAsset!!)
                scene.indexAsset(asset)
            }
        }

        if(!scene.folder.containsChild(asset.file.handle)) {
            FileOps.addChildToDir(scene.folder.handle,asset.file.handle)
        }
        val manager : ProjectManager = inject()
        serializeHandle(asset,manager.currentProject!!,scene)
        }
        post(AssetIncludedEvent(asset,scene))
        return loadedAsset!!
    }

    override fun serializeHandle(asset: AssetHandle, project:Project) : Result<Unit> {
        info("Attempting to serialize asset handle: $asset, extras: ${asset.extras}")
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

        val res = try { file.writeString(asset.serialize(),false) } catch (e: Exception) {
            return Result.failure(e)
        }
        val assetFile = try { file.writeString(encodestring(asset.extras),false) } catch (e: Exception) {
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
        val ext = asset.file.extension
        val filetype = FileType.parse(ext)
        val type = AssetType.fromFiletype(filetype)

        val asset = when (type) {
                AssetType.Model -> {
                    val model = ModelAsset(asset.file,asset,asset)
//                    if (ext == "gltf" || ext == "glb") storage.loadAsync<SceneModel>(asset.pth)
                    model.load()
                     model
                }
                AssetType.Texture -> {
                    val texture = TextureAsset(asset.file)
                    texture.load()
                     texture
                }
                AssetType.Shader -> {
                    val shader = ShaderAsset(asset.file,asset.name)
                    shader.load()
                     shader
                }
                AssetType.Material -> {
                    val material = MaterialAsset(asset.file)
                    material.load()
                     material
                }
                else -> {
                    warn("Asset type not recognized: $type")
                    val file = FileAsset(asset.file)
                    file.load()
                     file
                }
            }

        asset.usages.depenciesToLoad.forEach { handle ->
            if (locateAsset(handle) == null) {
                indexHandle(handle,inject())
                queueAssetLoad(handle).invokeOnCompletion() {
                   KtxAsync.launch {
                       onRenderingThread {
                           includeAsset(handle,inject())
                       }
                   }

                }
            }
        }


        assetRegistry[type]!!.add(asset)
        return asset
    }

    fun locateAsset(asset: AssetHandle) : Asset<*>? {
        val type = AssetType.fromFiletype(FileType.parse(asset.file.extension))
        val scene: Scene = inject()
        println("locating asset handle in scene with uuid ${asset.uuid}")
        scene.assets.forEach { a ->
            println(a.handle)
        }
        return scene.assets.firstOrNull { it.handle.uuid == asset.uuid }
    }

    fun locateAsset(id: String) : Asset<*>? {
        val scene: Scene = inject()
        return scene.assets.firstOrNull { it.handle.uuid == id }
    }

    override fun createHandle(file: FileHandle): AssetHandle {
        return AssetHandle(file.path())
    }

    fun createHandle(name: String, type: AssetType) : AssetHandle {
        return AssetHandle(Paths.PROJECT_ASSET_DIR_FOR(inject<ProjectManager>().currentProject!!.name).child(type.name).child(name).handle.path())
    }

    fun createHandle(file: FileHandle,uuid: String? = null, extras: Extras) : AssetHandle {
        return AssetHandle(file.path(), uuid ?: UUID.randomUUID().toString(), extras.map)
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
        val projectIndex = { proj: Project -> Paths.PROJECT_ASSET_INDEX_FOR(proj.name) }
        val sceneIndex = { proj: Project, scene: Scene -> Paths.SCENE_SCOPE_ASSET_INDEX_DIR_FOR(proj.name, scene.name) }
        val assetDir = { proj: Project, type: AssetType -> Paths.PROJECT_ASSET_DIR_FOR(proj.name).child(type.name) }
    }

}