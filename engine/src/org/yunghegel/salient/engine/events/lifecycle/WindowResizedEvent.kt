package org.yunghegel.salient.engine.events.lifecycle

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class WindowResizedEvent(val width:Int,val height:Int) {

    interface Listener {
        @Subscribe
        fun onWindowResized(event: WindowResizedEvent)

    }

}

fun onWindowResized(once: Boolean = false , action: (WindowResizedEvent) -> Unit) = object : WindowResizedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onWindowResized(event: WindowResizedEvent) {
        action(event)
    }
}

