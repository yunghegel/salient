package org.yunghegel.salient.engine.events

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.ecs.System

class SystemLoadedEvent(val system: System<*,*>) {

    interface Listener {
        @Subscribe
        fun onSystemLoaded(event: SystemLoadedEvent)

    }

}

fun onSystemLoaded(action: (SystemLoadedEvent) -> Unit) = object : SystemLoadedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onSystemLoaded(event: SystemLoadedEvent) {
        action(event)
    }
}