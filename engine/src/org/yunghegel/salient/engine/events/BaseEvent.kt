package org.yunghegel.salient.engine.events

open class BaseEvent {

    val message : String
        get() = this::class.simpleName ?: "BaseEvent"

}