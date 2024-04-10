package org.yunghegel.salient.engine.events

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class ProjectDiscoveryEvent() {

    interface Listener
    {
        @Subscribe
        fun onProjectDiscovery(event: ProjectDiscoveryEvent)

    }

}

fun onProjectDiscovery(action: ()->Unit) = object : ProjectDiscoveryEvent.Listener {

    init {
        Bus.register(this)
    }
    
    @Subscribe
    override fun onProjectDiscovery(event: ProjectDiscoveryEvent) {
        action()
    }
}