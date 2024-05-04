package org.yunghegel.salient.engine.ui.scene2d

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import org.yunghegel.gdx.utils.ext.getBounds
import org.yunghegel.salient.engine.scene3d.component.BoundsComponent.Companion.getBounds

class TreeNodeTable(val obj: Any,var widthSupplier: (()->Float)?=null) : STable() {

    val bounds : Rectangle = Rectangle()
    val overflowButton = SImageButton("overflow-menu")

    private var icon: Actor? = null
    private var label: SLabel? = null

    fun createIcon(actor: Actor) {
        icon = actor
    }

    fun createLabel(text:String) {
        label = SLabel(text)
    }

    fun build() {
        if (icon!=null) add(icon).size(16f)
        if (label!=null) add (label).growX()
        add(overflowButton).growX().right()
    }

    override fun layout(){
        getBounds(bounds)
        super.layout()
    }

    override fun getPrefWidth(): Float {
        return widthSupplier?.let { it() } ?: super.getPrefWidth()
    }
}