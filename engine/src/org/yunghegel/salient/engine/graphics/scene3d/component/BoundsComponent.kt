package org.yunghegel.salient.engine.graphics.scene3d.component

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.math.collision.BoundingBox
import ktx.collections.GdxArray
import org.yunghegel.debug.*
import org.yunghegel.gdx.utils.ext.collectNodes
import org.yunghegel.gdx.utils.ext.createBoundsRenderable
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.graphics.scene3d.GameObject
import org.yunghegel.salient.engine.graphics.scene3d.SceneContext

class BoundsComponent(bounds: GdxArray<BoundingBox>,go: GameObject) : EntityComponent<GdxArray<BoundingBox>>(null, bounds,go), Drawable {

    val renderable: GdxArray<RenderableProvider> = GdxArray()

    override val renderer: Boolean = true

    init {
        value?.each { box->
            renderable.add(createBoundsRenderable(box))
        }
    }

    override fun render(batch: ModelBatch, camera: Camera, context: SceneContext) {
        batch.render(renderable)
    }

    override fun renderDebug(debugContext: DebugContext) {
        with(debugContext) {
            renderable.each { renderable ->

            }
        }
    }

    override val mask: Int = maskOf(RENDER, IS_3D, USES_MODEL_BATCH, AFTER_DEPTH)

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