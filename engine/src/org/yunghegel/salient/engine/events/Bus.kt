package org.yunghegel.salient.engine.events

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBusBuilder
import org.yunghegel.gdx.utils.reflection.annotatedWith
import org.yunghegel.salient.engine.events.Bus.Config.eventBusBuilder
import org.yunghegel.salient.engine.events.Bus.emit
import org.yunghegel.salient.engine.system.Log
import org.yunghegel.salient.engine.system.debug
import org.yunghegel.salient.engine.system.emitEvent
import org.yunghegel.salient.engine.system.error
import org.yunghegel.salient.engine.system.event
import java.util.logging.Level
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object Bus {

    val emitter = EmitContext()

    val eventBus: EventBus by lazy { Config.eventBusBuilder.build()  }

    fun post (event: Any,log: Boolean = true) {
        if (log) if (event::class.annotatedWith(Event::class) && event is BaseEvent) event(event) else event(event::class.java)
        eventBus.post(event)
    }

    fun register (subscriber: Any) {
        eventBus.register(subscriber)
    }

    fun unregister (subscriber: Any) {
        eventBus.unregister(subscriber)
    }

    fun emit(emission: String,log: Boolean = true) {
        if (log) emitEvent(emission)
        emitter.emit(emission)
    }

    fun on(emission: String, listener: (String)->Unit) {
        emitter.on(emission,listener)
    }

    object Config {

        val eventBusBuilder: EventBusBuilder = EventBus.builder().apply {
                this.logger(object : org.greenrobot.eventbus.Logger {

                    override fun log(level: Level?, msg: String?) {
                        debug("${level?.name} $msg")
                    }

                    override fun log(level: Level?, msg: String?, th: Throwable?) {
                        error("${level?.name} $msg $th.message")
                    }
                })
                logNoSubscriberMessages(true)
                logSubscriberExceptions(true)

            }
        }



    }

    class EmitContext {

        val emitterListeners = mutableMapOf<String,MutableList<EmitterListener>>()

        fun on(emission: String, listener: (String)->Unit) {
            val listeners = emitterListeners[emission] ?: mutableListOf()
            listeners.add(EmitterListener(listener))
            emitterListeners[emission] = listeners
        }

        fun emit(emission: String) {
            val listeners = emitterListeners[emission] ?: return
            listeners.forEach { it.listener(emission) }
        }



    }

    class EmitterListener(val listener: (String)->Unit)

    class AutoEmitter<T:Any>(var source : T) : ReadWriteProperty<Any, T> {

        override fun getValue(thisRef: Any, property: kotlin.reflect.KProperty<*>): T {
            return source
        }

        override fun setValue(thisRef: Any, property: kotlin.reflect.KProperty<*>, value: T) {
            source = value
            emit(buildEmit(property,thisRef))

        }

        fun buildEmit(prop:KProperty<*>,ref: Any) : String {
            return "${ref::class.simpleName}.${prop.name}"
        }

    }


