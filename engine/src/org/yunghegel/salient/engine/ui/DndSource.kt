package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Null

interface DndSource<T> {

    fun endDrag(x:Float, y:Float, source: DragAndDrop.Source,payload: DragAndDrop.Payload)

    fun startDrag(x:Float, y:Float, source: DragAndDrop.Source) : TypedPayload<T>

    fun whileDragging(x:Float, y:Float, source: DragAndDrop.Source, payload: DragAndDrop.Payload)

    class Builder<T>() {

        var end: ((Float, Float, DragAndDrop.Source, DragAndDrop.Payload)->Unit)? = null

        var start: ((Float, Float, DragAndDrop.Source)->TypedPayload<T>)? = null

        var drag: ((Float, Float)->Unit)? = null

        fun endDrag(action: (Float, Float, DragAndDrop.Source, DragAndDrop.Payload)->Unit) {
            this.end = action
        }

        fun startDrag(action: (Float, Float, DragAndDrop.Source)->TypedPayload<T>) {
            this.start = action
        }

        fun whileDragging(action: (Float, Float)->Unit) {
            this.drag = action
        }

        fun makeSource(actor:Actor) : DragAndDrop.Source {
            return object : DragAndDrop.Source(actor) {
                override fun dragStart(event: InputEvent, x: Float, y: Float, pointer: Int): DragAndDrop.Payload {
                    val payload = start?.invoke(x, y, this) ?: TypedPayload()
                    return payload
                }

                override fun drag(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                    drag?.let { fn -> fn(x, y) }
                }

                override fun dragStop(
                    event: InputEvent?,
                    x: Float,
                    y: Float,
                    pointer: Int,
                    @Null payload: DragAndDrop.Payload?,
                    @Null target: DragAndDrop.Target?
                ) {
                    end?.let { fn -> fn(x, y, this, payload!!) }
                }
            }
        }

        fun build() : DndSource<T> {
            return object : DndSource<T> {
                override fun endDrag(x: Float, y: Float, source: DragAndDrop.Source, payload: DragAndDrop.Payload) {
                    end?.let { fn -> fn(x, y, source, payload) }
                }

                override fun startDrag(x: Float, y: Float, source: DragAndDrop.Source): TypedPayload<T> {
                    return start?.invoke(x, y, source) ?: TypedPayload()
                }

                override fun whileDragging(x: Float, y: Float, source: DragAndDrop.Source, payload: DragAndDrop.Payload) {
                    drag?.let { fn -> fn(x, y) }
                }
            }
        }
    }

    companion object {

            fun <T> build(init: Builder<T>.() -> Unit) : DndSource<T> {
                return Builder<T>().apply(init).build()
            }
    }

}