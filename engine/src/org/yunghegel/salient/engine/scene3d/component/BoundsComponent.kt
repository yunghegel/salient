package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.math.collision.BoundingBox
import ktx.collections.GdxArray
import org.yunghegel.debug.*
import org.yunghegel.gdx.renderer.util.maskOf
import org.yunghegel.gdx.utils.ext.collectNodes
import org.yunghegel.gdx.utils.ext.createBoundsRenderable
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.api.flags.DRAW_BOUNDS
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.SceneContext

class BoundsComponent(bounds: GdxArray<BoundingBox>,go: GameObject) : EntityComponent<GdxArray<BoundingBox>>(bounds,go){

    val renderable: GdxArray<RenderableProvider> = GdxArray()

    override val renderer: Boolean = true

    init {
        value?.each { box->
            renderable.add(createBoundsRenderable(box))
        }

        implements(debug)
        debugCondition { go.has(DRAW_BOUNDS) }
        val transform = go.getComponent(TransformComponent::class.java)
        renderable.each { renderable ->
            if (renderable is ModelInstance) {
                renderable.transform = transform?.value
            }
        }

    }

    context(SceneContext) override fun renderDebug(delta: Float) {
        renderable.each { renderable ->
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