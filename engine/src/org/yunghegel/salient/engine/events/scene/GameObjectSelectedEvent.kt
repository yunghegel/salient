package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.graphics.scene3d.GameObject

class GameObjectSelectedEvent(val go: List<GameObject>) {

    interface Listener {
        @Subscribe
        fun onGameObjectSelected(event: GameObjectSelectedEvent)

    }

}

fun onGameObjectSelected(action: (GameObjectSelectedEvent) -> Unit) = object : GameObjectSelectedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onGameObjectSelected(event: GameObjectSelectedEvent) {
        action(event)
    }
}