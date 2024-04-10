package org.yunghegel.salient.engine.api.scene

import com.badlogic.gdx.utils.Disposable
import org.yunghegel.salient.engine.api.RendererRoutine
import org.yunghegel.salient.engine.api.ResizeRoutine
import org.yunghegel.salient.engine.api.UpdateRoutine
import org.yunghegel.salient.engine.api.EditorSceneManager
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.graphics.scene3d.SceneContext

abstract class EditorScene(val ref: SceneHandle, val sceneManager: EditorSceneManager<*>) : UpdateRoutine,
    RendererRoutine, ResizeRoutine, Disposable {

    val assetUsage : MutableList<AssetHandle> = mutableListOf()

    abstract val renderer: EditorSceneRenderer<*,*>

    abstract val sceneGraph: EditorSceneGraph

    abstract val context: SceneContext








}