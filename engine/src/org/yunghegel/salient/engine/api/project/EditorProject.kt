package org.yunghegel.salient.engine.api.project

import com.badlogic.gdx.files.FileHandle
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ktx.assets.toAbsoluteFile
import org.yunghegel.salient.engine.api.EditorProjectManager
import org.yunghegel.salient.engine.api.properties.NamedObjectResource
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.system.debug
import org.yunghegel.salient.engine.system.file.Paths
import org.yunghegel.salient.engine.system.info


abstract class EditorProject<P:EditorProject<P,S>,S:EditorScene>(val handle: org.yunghegel.salient.engine.api.model.ProjectHandle) : NamedObjectResource  {



    @Transient
    abstract val projectManager: EditorProjectManager<P,S>

    @Transient
    open var currentScene : S? = null

    @Transient
    val scenes : MutableList<S> = mutableListOf()

    val sceneIndex = mutableListOf<SceneHandle>()

    val assetIndex = mutableListOf<AssetHandle>()

    val folder : FileHandle
        get() = projectManager.projectDir.child(handle.name)

    fun indexScene(handle: SceneHandle) {
        if (!sceneIndex.contains(handle)) sceneIndex.add(handle).also { debug("Indexed scene ${handle.name}") }
    }

    fun indexAsset(handle: AssetHandle) {
        if (!assetIndex.contains(handle)) {
            assetIndex.add(handle).also { debug("Indexed asset ${handle.name}") }
        }
        if (!Paths.PROJECT_ASSET_INDEX_FOR(name).containsChild("${handle.uuid}.uuid".toAbsoluteFile())) {
            info("Asset not indexed in project [$name], indexing now")
            Paths.PROJECT_ASSET_INDEX_FOR(name).child("${handle.uuid}.uuid").create().write(handle.toString())
        }
    }

    abstract fun initalizeScene(scene: S)

    fun validate(asset: AssetHandle) {
        scenes
    }



}