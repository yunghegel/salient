package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import net.mgsx.gltf.scene3d.scene.SceneManager
import net.mgsx.gltf.scene3d.scene.SceneRenderableSorter
import net.mgsx.gltf.scene3d.shaders.PBRDepthShaderProvider
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider
import org.yunghegel.salient.engine.api.scene.ObjectRenderer
import org.yunghegel.salient.engine.scene3d.SceneContext

class BasicRenderer(cam: PerspectiveCamera, context: SceneContext) : SceneManager(PBRShaderProvider(PBRShaderProvider.createDefaultConfig()), PBRDepthShaderProvider(PBRDepthShaderProvider.createDefaultConfig())
), ObjectRenderer {

    init {
        environment = context
        camera = cam
    }

    override fun renderColor(renderable: RenderableProvider, context: SceneContext) {
        renderableProviders.add(renderable)
        render()
        renderableProviders.clear()
    }

    override fun renderDepth(renderable: RenderableProvider, context: SceneContext) {
        renderableProviders.add(renderable)
        renderDepth()
        renderableProviders.clear()
    }

}