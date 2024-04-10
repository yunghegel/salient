package org.yunghegel.salient.engine.events.asset

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class AssetAddedEvent() {

    interface Listener
    {
        @Subscribe
        fun onAssetAdded(event: AssetAddedEvent)

    }

}

fun onAssetAdded(action: ()->Unit) = object : AssetAddedEvent.Listener {

    init {
        Bus.register(this)
    }
    
    @Subscribe
    override fun onAssetAdded(event: AssetAddedEvent) {
        action()
    }
}