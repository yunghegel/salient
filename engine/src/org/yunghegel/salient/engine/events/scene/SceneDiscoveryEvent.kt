package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.model.SceneHandle
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.events.Event

@Event("scene.discovery", [SceneHandle::class])
class SceneDiscoveryEvent(val handle: SceneHandle) {

    interface Listener {
        @Subscribe
        fun onSceneDiscovery(event: SceneDiscoveryEvent)

    }

}

fun onSceneDiscovery(action: (SceneDiscoveryEvent) -> Unit) = object : SceneDiscoveryEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onSceneDiscovery(event: SceneDiscoveryEvent) {
        action(event)
    }
}