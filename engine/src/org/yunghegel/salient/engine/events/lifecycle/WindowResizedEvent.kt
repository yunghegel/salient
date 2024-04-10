package org.yunghegel.salient.engine.events.lifecycle

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class WindowResizedEvent() {

    interface Listener
    {
        @Subscribe
        fun onWindowResized(event: WindowResizedEvent)

    }

}

fun onWindowResized(action: ()->Unit) = object : WindowResizedEvent.Listener {

    init {
        Bus.register(this)
    }
    
    @Subscribe
    override fun onWindowResized(event: WindowResizedEvent) {
        action()
    }
}