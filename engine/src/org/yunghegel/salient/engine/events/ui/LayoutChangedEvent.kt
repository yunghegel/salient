package org.yunghegel.salient.engine.events.ui

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class LayoutChangedEvent() {

    interface Listener {
        @Subscribe
        fun onLayoutChanged(event: LayoutChangedEvent)

    }

}

fun onLayoutChanged(action: () -> Unit) = object : LayoutChangedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onLayoutChanged(event: LayoutChangedEvent) {
        action()
    }
}