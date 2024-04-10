package org.yunghegel.salient.engine.events.lifecycle

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class ShutdownEvent {

    interface Listener
    {
        @Subscribe
        fun onShutdown(event: ShutdownEvent)

    }

}

fun onShutdown(action: ()->Unit) = object : ShutdownEvent.Listener {

    init {
        Bus.register(this)
    }
    @Subscribe
    override fun onShutdown(event: ShutdownEvent) {
        action()
    }
}