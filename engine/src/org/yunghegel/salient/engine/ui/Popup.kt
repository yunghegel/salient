package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import ktx.actors.onChange
import ktx.actors.onTouchUp


class Popup : PopTable() {

    init {
        isDraggable = false
        isHideOnUnfocus = true
        key(Input.Keys.ESCAPE, this::hide)
        isKeepSizedWithinStage = true
        addListener(object : TableShowHideListener() {
            override fun tableShown(event: Event?) {
//                Gdx.input.inputProcessor = UI.DialogStage
            }

            override fun tableHidden(event: Event?) {
                Gdx.input.inputProcessor = stage
            }
        })
    }

    fun init(actor: Actor) {
        attachToActor(actor, Align.top, Align.top)
        show(UI)
    }

}

fun Actor.popup(action : Popup.()->Unit) {
    onChange {
        val popup = Popup()
        popup.action()
        popup.init(this)
    }
}

fun Popup.option(text: String, action : ()->Unit) {
    val option = textbutton(text, action)
    option.onTouchUp { hide() }
    add(option).growX().row()
}