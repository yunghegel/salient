package org.yunghegel.salient.engine.scene3d.events

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.scene3d.GameObject

class GameObjectAddedEvent(val go: GameObject) {

    interface Listener {
        @Subscribe
        fun onGameObjectAdded(event: GameObjectAddedEvent)

    }

}

fun onGameObjectAdded(action: (GameObjectAddedEvent) -> Unit) = object : GameObjectAddedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onGameObjectAdded(event: GameObjectAddedEvent) {
        action(event)
    }
}