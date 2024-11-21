package org.yunghegel.salient.engine.ui.scene2d

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.kotcrab.vis.ui.widget.VisDialog
import ktx.actors.onChange
import ktx.actors.onClick


class SDialog(val title: String) : VisDialog(title, "default")  {

    val result : MutableMap<String,Any> = mutableMapOf()

    private fun centerOn(actor: Actor) {
        setPosition(actor.x + actor.width / 2 - width / 2, actor.y + actor.height / 2 - height / 2)
        setCenterOnAdd(false)
    }

    fun show(actor: Actor) {
        centerOn(actor)
        show(actor.stage)
    }

    fun actor(actor: Actor, key: String, mapResult: (Actor)->Any) {
        actor.onChange {
            result[key] = mapResult(actor)
        }
        contentTable.add(actor).growX().row()
    }

    fun actor(actor: Actor, key: String, mapResult: (Actor)->Any, setup: Cell<Actor>.()->Unit) {
        actor.onChange {
            result[key] = mapResult(actor)
        }
        with(contentTable.add(actor).growX()) {
            setup()
            row()
        }

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