package org.yunghegel.salient.engine.ui.widgets.menu

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.PopupMenu
import com.ray3k.stripe.PopTable
import com.ray3k.stripe.PopTableClickListener
import ktx.actors.onKey
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.pop
import java.awt.SystemColor.menu

open class ContextMenu() : PopTableClickListener() {

    val options = mutableMapOf<String,ContextItem>()
    val popup = getPopTable()

    constructor(actor: Actor) : this() {
        attachListener(actor)
    }

    init {
        popup.isHideOnUnfocus = true
        popup.isModal = false
        popup.isAttachToMouse = true
    }

    fun attachListener(actor:Actor) {
        actor.touchable = Touchable.enabled
        popup.attachToActor(actor, Align.bottomRight, Align.bottomLeft)
        actor.addListener(this)

    }

    fun addOption(text: String, icon: String?=null, action: ()->Unit) {
        val item = if (icon==null) ContextItem(text,action) else ContextItem(text,icon,action)
        options[text] = item
        popup.add(item).growX().row()
    }

    operator fun set(text: String,action:()->Unit) {
        addOption(text,null,action)
    }

    operator fun set(text: String,icon:String,action:()->Unit) {
        addOption(text,icon,action)
    }

    override fun clicked(event: InputEvent, x: Float, y: Float) {
         return
    }

    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
        if (button == 1) {
            super.clicked(event, x, y)
        }
        return super.touchDown(event, x, y, pointer, button)
    }
}