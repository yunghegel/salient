package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop

interface DndTarget<T> {

    val actor : Actor

    fun shouldAccept(payload: DragAndDrop.Payload) : Boolean

    fun handleDrop(obj: T)

    fun handleDrag(payload: DragAndDrop.Payload, x:Int, y:Int)

    fun onHover(actor: Actor) {}

    class Builder<T>(val target:Actor) {

        var accept : ((payload: DragAndDrop.Payload)->Boolean)? = null

        var handledrop: ((T)->Unit)?= null

        var onhover: ((Actor)->Unit)? = null

        var drag: ((DragAndDrop.Payload, Int, Int)->Unit)? = null

        fun shouldAccept(action: (payload: DragAndDrop.Payload)->Boolean) {
            this.accept = action
        }

        fun handleDrop(action : (T)->Unit) {
            this.handledrop = action
        }

        fun onHover(action: (Actor)->Unit) {
            this.onhover = action
        }

        fun handleDrag(action: (DragAndDrop.Payload, Int, Int)->Unit) {
            this.drag = action
        }

        fun  build() : DndTarget<T> {
            return object : DndTarget<T> {
                override val actor: Actor = target

                override fun shouldAccept(payload: DragAndDrop.Payload): Boolean {
                    if (accept!=null) return accept!!.invoke(payload)
                    return false
                }

                override fun handleDrop(obj: T) {
                    handledrop?.let { fn -> fn(obj)}
                }

                override fun onHover(actor: Actor) {
                    onhover?.let { fn -> fn(actor) }
                }

                override fun handleDrag(payload: DragAndDrop.Payload, x: Int, y: Int) {
                    drag?.let { fn -> fn(payload, x, y) }
                }
            }
        }

    }
}