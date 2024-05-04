package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.utils.Array
import ktx.collections.GdxArray
import org.yunghegel.debug.*
import org.yunghegel.gdx.renderer.util.maskOf
import org.yunghegel.salient.engine.ui.Icon
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.api.flags.DRAW_WIREFRAME
import org.yunghegel.salient.engine.ui.icon
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.SceneContext
import org.yunghegel.salient.engine.helpers.TextRenderer.camera
import org.yunghegel.salient.engine.helpers.WireBatch

class MeshComponent(mesh:GdxArray<Mesh>,go: GameObject) : EntityComponent<Array<Mesh>>(mesh,go),
    Icon by icon("mesh") {







    val model : RenderableProvider? by lazy { go.get(RenderableComponent::class)?.value }


    init {
        implements(debug)
        debugCondition { model != null }
        debugCondition { go.has(DRAW_WIREFRAME)}
    }

    context(SceneContext) override fun renderDebug(delta: Float) {
        batch.begin(camera)
        batch.render(model)
        batch.end()
    }

    companion object {
        val batch = WireBatch()
    }

}
