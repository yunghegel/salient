package org.yunghegel.salient.engine.api.project

import org.yunghegel.salient.engine.api.*
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.io.debug

abstract class EditorProject<P:EditorProject<P,S>,S:EditorScene>(val handle: ProjectHandle, val projectManager: EditorProjectManager<P,S>) : NamedObjectResource by handle {



    abstract var currentScene : S?

    abstract val scenes : MutableList<S>

    val sceneIndex = mutableListOf<SceneHandle>()

    val assetIndex = mutableListOf<AssetHandle>()

    fun indexScene(handle: SceneHandle) {
        if (!sceneIndex.contains(handle)) sceneIndex.add(handle).also { debug("Indexed scene ${handle.name}") }
    }

    abstract fun initalizeScene(scene: S)



}