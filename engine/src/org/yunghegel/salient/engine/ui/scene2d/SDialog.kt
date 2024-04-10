package org.yunghegel.gdx.utils.ui.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.kotcrab.vis.ui.widget.VisDialog


class SDialog(val title: String) : VisDialog(title, "default")  {

    fun centerOn(actor: Actor) {
        setPosition(actor.x + actor.width / 2 - width / 2, actor.y + actor.height / 2 - height / 2)
        setCenterOnAdd(false)
    }

    fun show(actor: Actor) {
        centerOn(actor)
        show(actor.stage)
    }

    fun autoremove() {
        addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                remove()
                return super.touchDown(event, x, y, pointer, button)
            }

            override fun keyTyped(event: InputEvent?, character: Char): Boolean {
                remove()
                return super.keyTyped(event, character)
            }
        })
    }

}