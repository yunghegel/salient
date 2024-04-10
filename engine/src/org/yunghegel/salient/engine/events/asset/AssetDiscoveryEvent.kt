package org.yunghegel.salient.engine.events.asset

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class AssetDiscoveryEvent() {

    interface Listener
    {
        @Subscribe
        fun onAssetDiscovery(event: AssetDiscoveryEvent)

    }

}

fun onAssetDiscovery(action: ()->Unit) = object : AssetDiscoveryEvent.Listener {

    init {
        Bus.register(this)
    }
    
    @Subscribe
    override fun onAssetDiscovery(event: AssetDiscoveryEvent) {
        action()
    }
}