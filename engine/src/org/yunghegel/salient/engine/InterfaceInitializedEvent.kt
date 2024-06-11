package org.yunghegel.salient.engine

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class InterfaceInitializedEvent() {

    interface Listener {
        @Subscribe
        fun onInterfaceInitialized(event: InterfaceInitializedEvent)

    }

}

fun onInterfaceInitialized(action: () -> Unit) = object : InterfaceInitializedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onInterfaceInitialized(event: InterfaceInitializedEvent) {
        action()
    }
}