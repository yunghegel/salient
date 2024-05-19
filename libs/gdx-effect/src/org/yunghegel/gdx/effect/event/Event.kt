package org.yunghegel.gdx.effect.event

import org.yunghegel.gdx.effect.EventBus

typealias EventConsumer = (EventPayload) -> Unit


typealias AsyncEventListener = suspend (event :Event) -> EventPayload
typealias EventListener = (event: Event) -> EventPayload

abstract class Event(vararg val args: Any) {

    var async = false

    val payload : EventPayload = EventPayload(args)

    val type : Class<out Event>
        get () = this::class.java

    val constructorTypes : Array<Class<*>>
        get() = type.constructors[0].parameterTypes

    operator fun invoke(vararg args :Any) : Event {
        val ev = type.constructors[0].newInstance(*args) as Event
        return ev
    }

    operator fun invoke(listener : EventListener) {

    }

    operator fun invoke (listener : AsyncEventListener) {

    }

}