package org.yunghegel.salient.engine.graphics.scene3d.events

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.graphics.scene3d.GameObject

class GameObjectRemovedEvent(val go: GameObject) {

    interface Listener {
        @Subscribe
        fun onGameObjectRemoved(event: GameObjectRemovedEvent)

    }

}

fun onGameObjectRemoved(action: (go: GameObjectRemovedEvent) -> Unit) = object : GameObjectRemovedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onGameObjectRemoved(event: GameObjectRemovedEvent) {
        action(event)
    }
}