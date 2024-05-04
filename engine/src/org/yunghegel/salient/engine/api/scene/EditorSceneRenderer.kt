package org.yunghegel.salient.engine.api.scene

import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.SceneContext

interface EditorSceneRenderer<T:EditorScene, G:EditorSceneGraph> {

    fun renderGraph(scene:T,graph: G, context: SceneContext)

    fun updateGraph(scene: T, go: GameObject, context: SceneContext)

    fun renderContext(context: SceneContext)

    fun prepareContext(context: SceneContext, depth: Boolean)





}