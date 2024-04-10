package org.yunghegel.salient.editor.plugins.events

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class SystemLoadedEvent() {

    interface Listener
    {
        @Subscribe
        fun onSystemLoaded(event: SystemLoadedEvent)

    }

}

fun onSystemLoaded(action: ()->Unit) = object : SystemLoadedEvent.Listener {

    init {
        Bus.register(this)
    }
    
    @Subscribe
    override fun onSystemLoaded(event: SystemLoadedEvent) {
        action()
    }
}