package org.yunghegel.salient.engine.events.asset

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.events.Event

@Event("asset.unindexed", [AssetHandle::class])
class AssetUnindexedEvent(val handle:AssetHandle) {

    interface Listener {
        @Subscribe
        fun onAssetUnindexedEvent(event: AssetUnindexedEvent)

    }

}

fun onAssetUnindexed(action: (AssetUnindexedEvent) -> Unit) = object : AssetUnindexedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onAssetUnindexedEvent(event: AssetUnindexedEvent) {
        action(event)
    }
}