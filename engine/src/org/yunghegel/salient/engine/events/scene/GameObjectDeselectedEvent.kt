package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.events.Event
import org.yunghegel.salient.engine.scene3d.GameObject

@Event("gameobject.deselected", [GameObject::class])
class GameObjectDeselectedEvent(val gameObjects: List<GameObject>) {

    interface Listener {
        @Subscribe
        fun onGameObjectDeselected(event: GameObjectDeselectedEvent)

    }

}

fun onGameObjectDeselected(action: (GameObjectDeselectedEvent) -> Unit) = object : GameObjectDeselectedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onGameObjectDeselected(event: GameObjectDeselectedEvent) {
        action(event)
    }
}