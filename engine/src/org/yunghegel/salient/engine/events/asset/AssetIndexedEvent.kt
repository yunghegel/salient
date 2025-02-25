package org.yunghegel.salient.engine.events.asset

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.events.Event

@Event("asset.indexed", [AssetHandle::class])
class AssetIndexedEvent(val handle: AssetHandle) {

    interface Listener {
        @Subscribe
        fun onAssetIndexed(event: AssetIndexedEvent)

    }

}

fun onAssetIndexed(action: (AssetIndexedEvent) -> Unit) = object : AssetIndexedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onAssetIndexed(event: AssetIndexedEvent) {
        action(event)
    }
}