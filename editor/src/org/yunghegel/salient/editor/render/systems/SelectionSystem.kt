package org.yunghegel.salient.editor.render.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.RenderableProvider
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import org.yunghegel.gdx.utils.ext.clearColor
import org.yunghegel.gdx.utils.ext.clearDepth
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.salient.core.graphics.util.OutlineDepth
import org.yunghegel.salient.core.graphics.util.OutlineRenderer
import org.yunghegel.salient.editor.app.Gui
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.engine.Pipeline
import org.yunghegel.salient.engine.State
import org.yunghegel.salient.engine.graphics.scene3d.GameObject
import org.yunghegel.salient.engine.graphics.scene3d.component.RenderableComponent
import org.yunghegel.salient.engine.graphics.scene3d.component.SelectedComponent
import org.yunghegel.salient.engine.io.inject

class SelectionSystem : IteratingSystem(Family.all(SelectedComponent::class.java).get()) {

    val outlineRenderer = OutlineRenderer()
    val scene : Scene by lazy{ inject() }
    val pipeline : Pipeline
        get() = engine as Pipeline

    val batch : SpriteBatch = inject()

    val outlineDepth = OutlineDepth(true)

    var fbo = FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.width, Gdx.graphics.height, true)

    val gui : Gui = inject()



    override fun processEntity(entity: Entity?, deltaTime: Float) {


    }

    override fun update(deltaTime: Float) {
        pipeline.once(State.COLOR_PASS) {
            batch.begin()
            batch.draw(scene.renderer.depthFbo.colorBufferTexture, scene.context.viewport.screenX.toFloat(), 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
            batch.end()
        }

    }

}