package org.yunghegel.salient.engine.api.scene

import org.yunghegel.salient.engine.scene3d.SceneContext

interface ISceneRenderer {

    fun renderGraph(graph: ISceneGraph)

    fun renderContext(context: SceneContext)

}