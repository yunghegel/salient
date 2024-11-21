package org.yunghegel.gdx.utils.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import ktx.actors.onClick
import org.yunghegel.gdx.utils.TypedPayload
import org.yunghegel.gdx.utils.ext.dialog

class Trigger<A:Actor,T>(val label: String, val actor: A, val dialog: Actor, skin: Skin,val mapResult: DialogActionsBuilder<T>.()->Unit) : Table(skin){

    val pl = TypedPayload<T>(null)

    init {
        actor.onClick {
            val d = dialog(actor,label,skin)
            d.button("Cancel", pl)

        }
    }


    interface DialogActions<T> {

        fun closed(pl: TypedPayload<T>)

        fun accepted(pl: TypedPayload<T>)

        fun <A:Actor> input(a: A, pl: TypedPayload<T>)

    }

    class DialogActionsBuilder<T> {

        var closed : (TypedPayload<T>)->Unit = {}

        var accepted : (TypedPayload<T>)->Unit = {}

        var input : (Actor,TypedPayload<T>)->Unit = {_,_->}

        fun build() = object : DialogActions<T> {

            override fun closed(pl: TypedPayload<T>) {
                closed(pl)
            }

            override fun accepted(pl: TypedPayload<T>) {
                accepted(pl)
            }

            override fun <A: Actor> input(a: A, pl: TypedPayload<T>) {
                input(a,pl)
            }
        }
    }
}