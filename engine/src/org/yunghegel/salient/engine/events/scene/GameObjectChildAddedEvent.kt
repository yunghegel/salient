package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.graphics.scene3d.GameObject

class GameObjectChildAddedEvent(val parent: GameObject, val child: GameObject) {

    interface Listener {
        @Subscribe
        fun onGameObjectChildAdded(event: GameObjectChildAddedEvent)

    }

}

fun onGameObjectChildAdded(action: (GameObject, GameObject) -> Unit) = object : GameObjectChildAddedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onGameObjectChildAdded(event: GameObjectChildAddedEvent) {
        action(event.parent, event.child)
    }
}