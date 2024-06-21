package org.yunghegel.salient.engine.api.asset

import com.badlogic.gdx.files.FileHandle
import kotlinx.coroutines.Deferred
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.api.scene.EditorScene.Companion.folder
import org.yunghegel.salient.engine.system.info
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

interface EditorAssetManager<P,S> where P: EditorProject<P,S>, S: EditorScene {



    fun queueAssetLoad(asset: AssetHandle): Deferred<Any>

    /**
     * Initialize a newly loaded project by recovering the asset index from the project directory.
     * Assets are not loaded until [includeAsset] is called on (indexed) handle. This is to avoid loading
     * assets into memory that are not actually used in the project.
     * @param project The project to load the asset index for.
     */
    fun loadProjectIndex(project: P) : List<AssetHandle>

    /**
     * Load the asset index for a scene, i.e. the assets that are used in the scene.
     * This is called on all the scenes owned by a project when the project is bootstrapped.
     *
     * Note: Scenes are indexed with an extension "uuid" in its directory, with its filename being the uuid
     * of the asset to include.
     *
     * @param scene The scene to load the asset index for.
     */

    fun loadSceneIndex(scene: S,project: P) : List<AssetHandle>


    /**
     * Load the asset which an indexed handle actually represents into memory.
     * @param asset The handle to load.
     */
    fun loadAsset(asset: AssetHandle) : Asset<*>

    /**
     * Index an asset in the project directory.
     * @param asset The handle to index.
     * @param project The project to index the asset in.
     */
    fun indexHandle(asset: AssetHandle, project: P)

    /**
     * Import an asset handle from a filepath, i.e. deserialize it from a file.
     * @param file The file to deserialize the handle from.
     */
    fun deserializeHandle(file: FileHandle) : Result<AssetHandle>
    
    /**
     * Export an asset handle to a filepath, i.e. serialize it to a file, and index it in the project
     * files.
     * @param asset The handle to serialize.
     */
    fun serializeHandle(asset: AssetHandle, project: P) : Result<Unit>

    /**
     * Serialize an asset handle to a file, and index it in the scene files.
     * @param asset The handle to serialize.
     */

    fun serializeHandle(asset: AssetHandle, project: P, scene: S) : Result<Unit>


    /**
     * Include an asset (previously indexed) for usage in a scene, exposing the object it represents
     * as a dependency of the scene.
     */
    fun includeAsset(asset: AssetHandle, scene: S)

    /**
     * Preload a scenes existing dependencies by reading its asset index and loading the assets they reference.
     * Simply including an asset in a project does not necessarily mean it will be loaded into memory. This means
     * we do not load the assets discovered in [loadProjectIndex] until they are actually needed.
     *
     * This means nothing will be loaded on demand during runtime, unless we want to add new assets to a scene.
     * @param scene The scene to preload the assets for.
     */
    fun preloadSceneAssets(scene: S, project: P)

    fun createHandle(file: FileHandle) : AssetHandle





}