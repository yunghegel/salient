package org.yunghegel.salient.editor.render.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import org.yunghegel.salient.core.graphics.util.OutlineDepth
import org.yunghegel.salient.core.graphics.util.OutlineRenderer
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.modules.GFXModule
import org.yunghegel.salient.engine.scene3d.component.SelectedComponent
import org.yunghegel.salient.engine.system.inject

class SelectionSystem : IteratingSystem(Family.all(SelectedComponent::class.java).get()) {

    val outlineRenderer = OutlineRenderer()
    val scene : Scene by lazy{ inject() }
    val gfxModule : GFXModule by lazy{ inject() }


    val batch : SpriteBatch = inject()

    val outlineDepth = OutlineDepth(true)

    var fbo = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)



    override fun processEntity(entity: Entity?, deltaTime: Float) {


    }

    override fun update(deltaTime: Float) {
//        pipeline.once(State.UI_PASS) {
//            batch.begin()
//            batch.draw(scene.renderer.depthFbo.colorBufferTexture, 0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
//            batch.end()
//        }

    }

}