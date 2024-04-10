package org.yunghegel.salient.engine.events.lifecycle

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.events.Event

class StartupEvent : Event() {

    interface Listener
    {
        @Subscribe
        fun onStartup(event: StartupEvent)

    }

}

fun onStartup(action: ()->Unit) = object : StartupEvent.Listener {

    init {
        Bus.register(this)
    }
    @Subscribe
    override fun onStartup(event: StartupEvent) {
        action()
    }
}