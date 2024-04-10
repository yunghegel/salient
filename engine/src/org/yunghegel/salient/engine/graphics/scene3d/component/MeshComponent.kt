package org.yunghegel.salient.engine.graphics.scene3d.component

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.utils.Array
import ktx.collections.GdxArray
import org.yunghegel.debug.*
import org.yunghegel.salient.engine.api.Icon
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.api.icon
import org.yunghegel.salient.engine.graphics.scene3d.GameObject
import org.yunghegel.salient.engine.graphics.scene3d.SceneContext
import org.yunghegel.salient.engine.helpers.WireBatch

class MeshComponent(mesh:GdxArray<Mesh>,go: GameObject) : EntityComponent<Array<Mesh>>(null, mesh,go), Drawable,Icon by icon("mesh"){

    override val renderer: Boolean = true

    override val mask: Int = maskOf(RENDER, IS_3D, AFTER_DEPTH)

    override val shouldDraw: Boolean
        get() = model != null

    val model : RenderableProvider? by lazy { go.get(RenderableComponent::class)?.value }


    override fun renderDebug(debugContext: DebugContext) {
        batch.begin(debugContext.camera)
        batch.render(model)
        batch.end()
    }

    override fun render(batch: ModelBatch, camera: Camera, context: SceneContext) {

    }



    companion object {
        val batch = WireBatch()
    }

}
