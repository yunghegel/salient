package org.yunghegel.salient.engine.events.asset

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.events.Bus

class AssetIncludedEvent(val asset: AssetHandle, val scene: EditorScene) {

    interface Listener {
        @Subscribe
        fun onAssetIncluded(event: AssetIncludedEvent)

    }

}

fun onAssetIncluded(action: (AssetIncludedEvent) -> Unit) = object : AssetIncludedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onAssetIncluded(event: AssetIncludedEvent) {
        action(event)
    }
}