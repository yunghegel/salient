package org.yunghegel.gdx.utils.ui

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import ktx.actors.onEnter
import ktx.actors.onExit
import org.yunghegel.gdx.utils.TypedPayload

class DNDConfig {

    val acceptedSourceTypes = mutableListOf<Class<out Actor>>()

    val rejectCondtions = mutableListOf<(DragAndDrop.Payload) -> Boolean>()

    fun rejectIf(action: (DragAndDrop.Payload) -> Boolean) {
        rejectCondtions.add(action)
    }

    fun rejectIfSourceIs(type: Class<out Actor>) {
        rejectCondtions.add {payload -> type.isAssignableFrom(payload.dragActor::class.java)}
    }

    fun acceptSource(type: Class<out Actor>) {
        acceptedSourceTypes.add(type)
    }

    fun rejectif(boolean:()-> Boolean) {
        rejectCondtions.add {boolean()}
    }

}



fun <T,A:Actor> Actor.buildDrop (dnd: DragAndDrop, actor: A, payload: T, conf: TargetBuilder<A,T>.()->Unit) {
    val target = TargetBuilder<A,T>(actor)
    target.conf()
    dnd.addTarget(target())
}

@Suppress("UNCHECKED_CAST")
class TargetBuilder<T:Actor,Type>(val actor: T):(()->DragAndDrop.Target) {

    private var _shouldAccept : (TypedPayload<Type>) -> Boolean = {true}
    private var _handleDrop : (Type) -> Unit = {}
    private var _handleDrag : (TypedPayload<Type>, Int, Int) -> Unit = {_,_,_ ->}
    private var _onEnter : (T) -> Unit = {}
    private var _onExit : (T) -> Unit = {}
    private var _reset : (TypedPayload<Type>) -> Boolean = {false}


    val _config = DNDConfig()

    fun config(action: DNDConfig.() -> Unit) {
        _config.action()
    }

    fun shouldAccept(action: (TypedPayload<Type>) -> Boolean) {
        _shouldAccept = action
    }

    fun handleDrop(action: (Type?) -> Unit) {
        _handleDrop = action
    }

    fun handleDrag(action: (DragAndDrop.Payload, Int, Int) -> Unit) {
        _handleDrag = action
    }

    fun onEnter(action: (T) -> Unit) {
       _onEnter = action
    }

    fun onExit(action: (T) -> Unit) {
        _onExit = action
    }

    fun onReset(action: (TypedPayload<Type>) -> Boolean) {
        _reset = action
    }

    override fun invoke(): DragAndDrop.Target {

        actor.onExit(_onExit)
        actor.onEnter(_onEnter)

        return object : DragAndDrop.Target(actor) {
            override fun drag(source: DragAndDrop.Source, payload: DragAndDrop.Payload, x: Float, y: Float, pointer: Int): Boolean {
                _handleDrag(payload as TypedPayload<Type>, x.toInt(), y.toInt())

                return _config.rejectCondtions.all { it(payload) } && _shouldAccept(payload as TypedPayload<Type>) && _config.acceptedSourceTypes.any { it.isAssignableFrom(payload.dragActor::class.java) }

            }
            override fun drop(source: DragAndDrop.Source, payload: DragAndDrop.Payload, x: Float, y: Float, pointer: Int) {
                val pl = payload as TypedPayload<Type>
                _handleDrop(pl.value as Type)
            }

            override fun reset(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?) {
                if (_reset(payload as TypedPayload<Type>)) {
                    super.reset(source, payload)
                }
            }
        }
}
}

fun <T:Actor, Type> Actor.buildDrag(dnd: DragAndDrop, actor: T, payload: Type, conf: SourceBuilder<T,Type>.()->Unit) {
    val source = SourceBuilder<T,Type>(actor,payload)
    source.conf()

}

class SourceBuilder<T:Actor,Type>(val actor: Actor, val value: Type): ()->DragAndDrop.Source {


    private var _onDrag : (TypedPayload<Type>, Float, Float) -> Unit = {_,_,_ ->}
    private var _onDragStart : (TypedPayload<Type>, Float, Float) -> Unit = {_,_,_ ->}
    private var _onDragStop : (TypedPayload<Type>, Float, Float) -> Unit = {_,_,_ ->}
    private var _buildDragActor: (T,Type)->Actor = {_,_ -> actor}

    fun onDrag(action: (TypedPayload<Type>, Float, Float) -> Unit) {
        _onDrag = action
    }

    fun onDragStart(action: (TypedPayload<Type>, Float, Float) -> Unit) {
        _onDragStart = action
    }

    fun onDragStop(action: (TypedPayload<Type>, Float, Float) -> Unit) {
        _onDragStop = action
    }

    override fun invoke(): DragAndDrop.Source {
        return object : DragAndDrop.Source(actor) {
            override fun dragStart(event: InputEvent, x: Float, y: Float, pointer: Int): DragAndDrop.Payload {
                val payload = TypedPayload<Type>()
                payload.value = value
                payload.dragActor = _buildDragActor(actor as T, value)
                _onDragStart(payload, x, y)
                return payload
            }

            override fun dragStop(event: InputEvent, x: Float, y: Float, pointer: Int, payload: DragAndDrop.Payload, target: DragAndDrop.Target?) {
                _onDragStop(payload as TypedPayload<Type>, x, y)
            }

            override fun drag(event: InputEvent, x: Float, y: Float, pointer: Int) {
                val payload = super.drag(event, x, y, pointer)
                _onDrag(payload as TypedPayload<Type>, x, y)

            }
        }
    }

}
