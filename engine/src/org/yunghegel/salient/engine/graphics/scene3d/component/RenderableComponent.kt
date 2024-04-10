package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import org.yunghegel.salient.engine.RendererRoutine
import org.yunghegel.salient.engine.UpdateRoutine
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.ecs.EntityComponent
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.SceneContext
import org.yunghegel.salient.engine.sys.inject

class RenderableComponent (val renderableProvider: RenderableProvider) : EntityComponent<RenderableProvider>(RenderableProvider::class.java,renderableProvider),RenderableProvider by renderableProvider {

    override val renderer: Boolean = true

    override val updater: Boolean = true

    private val renderableElements = Array<Renderable>()

    override fun getRenderables(renderables: Array<Renderable>?, pool: Pool<Renderable>) {
        renderables?.addAll(this.renderableElements)
    }

    private val renderablesPool = mutableListOf<RenderableProvider>()

    override fun update(scene: EditorScene, go: GameObject, context: SceneContext) {
        renderablesPool.forEach { it.getRenderables(renderableElements, null) }
    }

    override fun render(batch: ModelBatch, camera: Camera, context: SceneContext) {
        batch.render(renderableProvider)

    }


}