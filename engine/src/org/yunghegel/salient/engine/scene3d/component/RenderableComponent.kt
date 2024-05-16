package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pool
import org.yunghegel.gdx.utils.ext.convertToPBR
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.api.flags.GameObjectFlags
import org.yunghegel.salient.engine.api.flags.RENDER
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.SceneContext

class RenderableComponent (val renderableProvider: RenderableProvider?,go: GameObject) : EntityComponent<RenderableProvider>(renderableProvider,go) {

    override val renderer: Boolean = true

    override val updater: Boolean = true

    private val renderableElements = Array<Renderable>()

    private val renderablesPool = mutableListOf<RenderableProvider>()

    init {
        implements(color)
        colorCondition { renderableProvider != null }
        colorCondition { go.has(RENDER)}
        if (renderableProvider is ModelInstance) {
            renderableProvider.materials.forEach { mat ->
                mat.forEach { println(it::class) }
                convertToPBR(mat)
            }
        }
        val transform = go.getComponent(TransformComponent::class.java)
        transform?.configure(this)

    }

    override fun update(scene: EditorScene, go: GameObject, context: SceneContext) {
        renderablesPool.forEach { it.getRenderables(renderableElements, null) }
    }

    context(SceneContext) override fun renderColor(delta: Float) {
        renderableProvider?.let { renderable ->
            modelBatch.render(renderable,go.scene.context)
        }
    }

    context(SceneContext) override fun renderDepth(delta: Float) {
        renderableProvider?.let { renderable ->
            depthBatch.render(renderable)
        }
    }
}