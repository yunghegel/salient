package org.yunghegel.salient.engine.events

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.EventBusBuilder
import org.yunghegel.salient.engine.system.event

object Bus {

    val eventBus: EventBus by lazy { Config.eventBusBuilder.build()  }

    fun post (event: Any,log: Boolean = true) {
        if (log) event(event::class.java)
        eventBus.post(event)
    }

    fun register (subscriber: Any) {
        eventBus.register(subscriber)
    }

    fun unregister (subscriber: Any) {
        eventBus.unregister(subscriber)
    }

    object Config {

        val eventBusBuilder: EventBusBuilder = EventBus.builder()

        init {
            eventBusBuilder.logNoSubscriberMessages(false)
            eventBusBuilder.sendNoSubscriberEvent(false)
            eventBusBuilder.logger(EventLog())
        }

    }

}