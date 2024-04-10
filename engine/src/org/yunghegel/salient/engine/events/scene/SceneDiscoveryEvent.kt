package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class SceneDiscoveryEvent() {

    interface Listener
    {
        @Subscribe
        fun onSceneDiscovery(event: SceneDiscoveryEvent)

    }

}

fun onSceneDiscovery(action: ()->Unit) = object : SceneDiscoveryEvent.Listener {

    init {
        Bus.register(this)
    }
    
    @Subscribe
    override fun onSceneDiscovery(event: SceneDiscoveryEvent) {
        action()
    }
}