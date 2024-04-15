package org.yunghegel.gdx.utils.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Container

class ToggleContainer<T:Actor>(actor: T? = null) : Container<T>(actor) {

    var tmp : T? = null

    fun toggle() {
        if(actor == null) {
            actor = tmp
        } else {
            tmp = actor
            actor = null
        }
        layout()
    }



}