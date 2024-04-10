package org.yunghegel.salient.engine.events.asset

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.events.Bus

class AssetAddedEvent(val asset: Asset<*>) {

    interface Listener {
        @Subscribe
        fun onAssetAdded(event: AssetAddedEvent)

    }

}

fun onAssetAdded(action: (AssetAddedEvent) -> Unit) = object : AssetAddedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onAssetAdded(event: AssetAddedEvent) {
        action(event)
    }
}