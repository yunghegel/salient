package org.yunghegel.salient.engine.api.scene

import com.badlogic.gdx.graphics.g3d.RenderableProvider
import org.yunghegel.salient.engine.scene3d.SceneContext

interface ObjectRenderer {



    fun renderDepth(renderable: RenderableProvider, context: SceneContext)

    fun renderColor(renderable: RenderableProvider, context: SceneContext)

}