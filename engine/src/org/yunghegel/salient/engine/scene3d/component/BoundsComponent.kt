package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.math.collision.BoundingBox
import ktx.collections.GdxArray
import org.yunghegel.gdx.utils.ext.collectNodes
import org.yunghegel.gdx.utils.ext.createBoundsRenderable
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.salient.engine.api.ecs.BaseComponent
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.api.flags.DRAW_BOUNDS
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.SceneContext
import kotlin.reflect.KClass

class BoundsComponent(bounds: GdxArray<BoundingBox>,go: GameObject) : EntityComponent<GdxArray<BoundingBox>>(bounds,go){

    val renderable: GdxArray<RenderableProvider> = GdxArray()
    override val type: KClass<out BaseComponent> = BoundsComponent::class
    override val renderer: Boolean = true
    val transform = go.getComponent(TransformComponent::class.java)

    init {
        value?.each { box->
            renderable.add(createBoundsRenderable(box))
        }

        implements(debug)
        debugCondition { go.has(DRAW_BOUNDS) }
        renderable.each { renderable ->
            if (renderable is ModelInstance) {
                renderable.transform = transform?.value
            }
        }

    }

    context(SceneContext) override fun renderDebug(delta: Float) {
        renderable.each { renderable ->
            if (renderable is ModelInstance) {
                renderable.transform =go.getTransform()
            }

            modelBatch.render(renderable)
        }
    }

    companion object {
        fun getBounds(model: Model) : GdxArray<BoundingBox> {
            val bounds = GdxArray<BoundingBox>()
            collectNodes(model).each { node ->
                val mesh = node.parts.firstOrNull()?.meshPart?.mesh
                if (mesh is Mesh) {
                    val bbox = BoundingBox()
                    mesh.calculateBoundingBox(bbox)
                    bbox.mul(node.globalTransform)
                    bbox.mul(model.nodes.first().globalTransform)
                    bounds.add(bbox)
                }
            }
            return bounds
        }
    }



}