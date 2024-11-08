package org.yunghegel.salient.engine.api.project

import com.badlogic.gdx.files.FileHandle
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.yunghegel.salient.engine.api.EditorProjectManager
import org.yunghegel.salient.engine.api.properties.NamedObjectResource
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.system.debug

@Serializable
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
        if (!assetIndex.contains(handle)) assetIndex.add(handle).also { debug("Indexed asset ${handle.name}") }
    }

    abstract fun initalizeScene(scene: S)

    fun validate(asset: AssetHandle) {
        scenes
    }



}