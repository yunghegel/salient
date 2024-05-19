package org.yunghegel.salient.engine.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import org.yunghegel.gdx.utils.TypedPayload
import org.yunghegel.gdx.utils.ext.ifInstance
import org.yunghegel.gdx.utils.ui.Hoverable

@Suppress("UNCHECKED_CAST")
interface DndTarget<T> {

    val actor : Actor

    fun shouldAccept(payload: TypedPayload<T>) : Boolean

    fun handleDrop(obj: T)

    fun handleDrag(payload: TypedPayload<T>, x:Int, y:Int)

    fun onHover(source: Actor, target: Hoverable.HoverQueryable) {}

    fun toTarget() : DragAndDrop.Target {
        val target = object : DragAndDrop.Target(actor) {
            override fun drag(
                source: DragAndDrop.Source?,
                payload: DragAndDrop.Payload?,
                x: Float,
                y: Float,
                pointer: Int
            ): Boolean {
                var accept = false
                source?.let { src ->
                    src.actor.ifInstance(Hoverable.HoverQueryable::class) { hoverable ->
                        if (hoverable.isHovered(x,y)) { onHover(actor,hoverable) }
                    }
                }
                payload?.takeIf{ payload is TypedPayload<*> }.let { pl ->
                    accept = shouldAccept(pl as TypedPayload<T>)
                    handleDrag(pl, x.toInt(), y.toInt())
                    pl.hoverable?.takeIf { it.isHovered(x,y) }.let { onHover(source?.actor!!, it!!) }
                }
                return accept
            }

            override fun drop(
                source: DragAndDrop.Source?,
                payload: DragAndDrop.Payload?,
                x: Float,
                y: Float,
                pointer: Int
            ) {
                payload?.ifInstance(TypedPayload::class) { pl ->
                    pl.run {handleDrop(pl.value as T)}
                }
            }

            override fun reset(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?) {
                super.reset(source, payload)
            }
        }
        return target
    }

    class Builder<T>(val target:Actor) {

        var accept : ((payload: DragAndDrop.Payload)->Boolean)? = null

        var handledrop: ((T)->Unit)?= null

        var onhover: ((Actor,Hoverable.HoverQueryable)->Unit)? = null

        var drag: ((DragAndDrop.Payload, Int, Int)->Unit)? = null

        fun shouldAccept(action: (payload: DragAndDrop.Payload)->Boolean) {
            this.accept = action
        }

        fun handleDrop(action : (T)->Unit) {
            this.handledrop = action
        }

        fun onHover(action: (Actor,Hoverable.HoverQueryable)->Unit) {
            this.onhover = action
        }

        fun handleDrag(action: (DragAndDrop.Payload, Int, Int)->Unit) {
            this.drag = action
        }

        fun  build() : DndTarget<T> {
            return object : DndTarget<T> {
                override val actor: Actor = target

                override fun shouldAccept(payload: TypedPayload<T>): Boolean {
                    if (accept!=null) return accept!!.invoke(payload)
                    return false
                }

                override fun handleDrop(obj: T) {
                    handledrop?.let { fn -> fn(obj)}
                }

                override fun onHover(source: Actor, target: Hoverable.HoverQueryable) {
                    onhover?.let { fn -> fn(source,target) }
                }

                override fun handleDrag(payload: TypedPayload<T>, x: Int, y: Int) {
                    drag?.let { fn -> fn(payload, x, y) }
                }
            }
        }

    }
}

fun <T:Actor,Object> T.createDropTarget(dnd:DragAndDrop,value: T, builder: DndTarget.Builder<Object>.()->Unit) : DragAndDrop.Target {
    val b = DndTarget.Builder<Object>(this)
    return b.apply(builder).build().toTarget()
}