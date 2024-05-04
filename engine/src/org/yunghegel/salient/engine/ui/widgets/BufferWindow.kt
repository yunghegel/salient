package org.yunghegel.salient.engine.ui.widgets

import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.utils.Scaling
import org.yunghegel.gdx.utils.ext.textureDrawable
import org.yunghegel.gdx.utils.temporalAction
import org.yunghegel.salient.engine.ui.scene2d.SImage
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.SWindow

class BufferWindow (name:String): SWindow(name) {

    val container : STable = STable()

    var fbo : FrameBuffer? = null

    init {
        add(container).grow()
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
            val image = SImage(textureDrawable(fbo.textureAttachments[i]))
            image.setSize(width,height)
            addBuffer(image)
        }
    }

    fun update() {
        if(fbo != null) {
            val image = SImage(textureDrawable(fbo!!.colorBufferTexture))
            image.setSize(width,height)
            setBuffer(image)
        }
    }

    val updateAction = temporalAction({update()}, 0.1f)

    override fun act(delta: Float) {
        super.act(delta)
        updateAction.update(delta)
    }

    fun show(x:Float,y:Float,width:Float,height:Float,stage:Stage) {
        setPosition(x,y)
        setSize(width,height)
        stage.addActor(this)
    }

}