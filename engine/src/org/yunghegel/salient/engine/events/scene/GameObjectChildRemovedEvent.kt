package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.graphics.scene3d.GameObject

class GameObjectChildRemovedEvent(val parent: GameObject, val child: GameObject) {

    interface Listener {
        @Subscribe
        fun onGameObjectChildRemoved(event: GameObjectChildRemovedEvent)

    }

}

fun onGameObjectChildRemoved(action: (GameObject,GameObject) -> Unit) = object : GameObjectChildRemovedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onGameObjectChildRemoved(event: GameObjectChildRemovedEvent) {
        action(event.parent, event.child)
    }
}