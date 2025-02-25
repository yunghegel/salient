package org.yunghegel.salient.engine.ui.widgets

import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.actors.onKeyDown
import org.yunghegel.gdx.utils.ext.textureDrawable
import org.yunghegel.gdx.utils.temporal
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImage
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.SWindow

class BufferWindow(name: String) : SWindow(name, "default", true) {

    val container : STable = STable()

    var fbo : FrameBuffer? = null

    init {
        add(container).grow()
        sizeTo(inject())
        onKeyDown() { key ->
            when(key) {
               Input.Keys.ESCAPE -> { remove();}
            }
        }
        container.touchable = Touchable.disabled
        touchable = Touchable.disabled
    }

    fun sizeTo(viewport: Viewport) {
        setSize(viewport.worldWidth,viewport.worldHeight)
        setPosition(viewport.screenX.toFloat(),viewport.screenY.toFloat())
    }

    fun addBuffer(image: SImage) {
        container.add(image).grow()
        image.setScaling(Scaling.fit)
    }

    fun setBuffer(image: SImage) {
    }

    fun create(fbo: FrameBuffer) {
        this.fbo = fbo
        val size = fbo.textureAttachments.size
        container.clear()
        for (i in 0 until size) {
            val tx = textureDrawable(fbo.colorBufferTexture)
            tx.region.flip(false,true)
            val image = SImage(tx)
            addBuffer(image)
        }
    }

    fun update() {
        if(fbo != null) {
            val tx = textureDrawable(fbo!!.colorBufferTexture)
            tx.region.flip(false,true)
            val image = SImage(tx)
            image.setSize(width,height)
            setBuffer(image)
        }
    }

    val updateAction = temporal(0.1f) { update() }

    override fun act(delta: Float) {
        super.act(delta)
        updateAction.update(delta)
    }

    fun show(x:Float,y:Float,width:Float,height:Float,stage:Stage) {
        setPosition(x,y)
        setSize(width,height)
        stage.addActor(this)
        sizeTo(UI.viewport)
    }

}