package org.yunghegel.salient.editor.plugins.outline.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import ktx.app.clearScreen
import ktx.ashley.get
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.core.graphics.util.OutlineDepth
import org.yunghegel.salient.editor.app.Gui
import org.yunghegel.salient.editor.app.Salient.Companion.once
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.editor.plugins.outline.lib.Outliner
import org.yunghegel.salient.engine.State
import org.yunghegel.salient.engine.api.flags.SELECTED
import org.yunghegel.salient.engine.events.lifecycle.onWindowResized
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.component.PickableComponent
import org.yunghegel.salient.engine.scene3d.component.RenderableComponent
import org.yunghegel.salient.engine.scene3d.component.SelectedComponent
import org.yunghegel.salient.engine.helpers.DepthBatch
import org.yunghegel.salient.engine.system.inject

class OutlineSystem(val outliner: OutlineDepth) : BaseSystem("outline_system", State.DEPTH_PASS.ordinal, Family.all(
    RenderableComponent::class.java ).get()) {

    val depthbatch : DepthBatch = DepthBatch(DepthShaderProvider())
    val batch : SpriteBatch = SpriteBatch()
    val gui : Gui = inject()

    var outlinefbo = FrameBuffer(Pixmap.Format.RGBA8888, appwidth, appheight,true)

    fun ensureFBO(w:Int,h:Int) {
        if (outlinefbo.width != w || outlinefbo.height != h) {
            outlinefbo.dispose()
            outlinefbo = FrameBuffer(Pixmap.Format.RGBA8888, w, h, true)
        }
    }

    override fun addedToEngine(engine: Engine?) {
        super.addedToEngine(engine)
        onWindowResized {
            ensureFBO(it.width, it.height)
        }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
    }

    override fun processEntity(p0: Entity?, p1: Float) {
        val go = p0 as GameObject
        if (!go.has(SELECTED)) return
        once(State.OVERLAY_PASS) {
            val pickable = go.get<RenderableComponent>()
            val depthtexture = pass(outlinefbo) {
                clearScreen(0f,0f,0f,0f)
                gui.updateviewport()
                with(scene.context) {
                    depthbatch.begin(perspectiveCamera)
                    depthbatch.render(pickable?.renderableProvider,scene.context)
                    depthbatch.end()
                }
            }
            Outliner.settings.run {
                outliner.render(inject(),depthtexture,scene.context.perspectiveCamera)
            }

        }



    }

}