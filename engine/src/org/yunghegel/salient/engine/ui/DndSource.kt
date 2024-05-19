package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Null
import org.checkerframework.checker.units.qual.A
import org.yunghegel.gdx.utils.TypedPayload

interface DndSource<A:Actor,T> {

    fun endDrag(x:Float, y:Float, source: DragAndDrop.Source,payload: DragAndDrop.Payload)

    fun startDrag(x:Float, y:Float, source: DragAndDrop.Source) : TypedPayload<T>

    fun whileDragging(x:Float, y:Float, source: DragAndDrop.Source, payload: DragAndDrop.Payload)

    fun createPayload(actor: A) : TypedPayload<T>

    class Builder<A:Actor,T>(val actor: Actor) {

        var end: ((Float, Float, DragAndDrop.Source, DragAndDrop.Payload)->Unit)? = null

        var start: ((Float, Float, DragAndDrop.Source)->TypedPayload<T>)? = null

        var drag: ((Float, Float)->Unit)? = null

        var makePayload: ((A) -> TypedPayload<T>)? = null

        fun endDrag(action: (Float, Float, DragAndDrop.Source, DragAndDrop.Payload)->Unit) {
            this.end = action
        }

        fun startDrag(action: (Float, Float, DragAndDrop.Source)->TypedPayload<T>) {
            this.start = action
        }

        fun whileDragging(action: (Float, Float)->Unit) {
            this.drag = action
        }

        fun makeSource() : DragAndDrop.Source {
            return object : DragAndDrop.Source(actor) {
                override fun dragStart(event: InputEvent, x: Float, y: Float, pointer: Int): DragAndDrop.Payload {
                    val payload = makePayload?.invoke(actor as A) ?:  start?.invoke(x, y, this) ?: TypedPayload()
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

        fun build() : DndSource<A,T> {
            return object : DndSource<A,T> {
                override fun endDrag(x: Float, y: Float, source: DragAndDrop.Source, payload: DragAndDrop.Payload) {
                    end?.let { fn -> fn(x, y, source, payload) }
                }

                override fun startDrag(x: Float, y: Float, source: DragAndDrop.Source): TypedPayload<T> {
                    return start?.invoke(x, y, source) ?: TypedPayload()
                }

                override fun whileDragging(x: Float, y: Float, source: DragAndDrop.Source, payload: DragAndDrop.Payload) {
                    drag?.let { fn -> fn(x, y) }
                }

                override fun createPayload(actor: A): TypedPayload<T> {
                    TODO("Not yet implemented")
                }
            }
        }
    }

    companion object {


    }

}

fun <T:Actor,Object> T.createDropSource(dnd: DragAndDrop, value: Object, builder: DndSource.Builder<T,Object>.()->Unit) : DragAndDrop.Source {
    val b = DndSource.Builder<T,Object>(this)
    return b.apply(builder).makeSource()
}