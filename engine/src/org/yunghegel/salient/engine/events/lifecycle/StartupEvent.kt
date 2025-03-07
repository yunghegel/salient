package org.yunghegel.salient.engine.events.lifecycle

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class StartupEvent {

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