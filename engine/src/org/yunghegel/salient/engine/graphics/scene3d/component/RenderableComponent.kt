package org.yunghegel.salient.engine.graphics.scene3d.component

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.graphics.scene3d.GameObject
import org.yunghegel.salient.engine.graphics.scene3d.SceneContext

class RenderableComponent (val renderableProvider: RenderableProvider,go: GameObject) : EntityComponent<RenderableProvider>(RenderableProvider::class.java,renderableProvider,go),RenderableProvider by renderableProvider {

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