package org.yunghegel.salient.engine.events.scene

import com.badlogic.ashley.core.Component
import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.scene3d.GameObject

class GameObjectComponentAddedEvent(val gameObject: GameObject, val component: Component) {

    interface Listener {
        @Subscribe
        fun onGameObjectComponentAdded(event: GameObjectComponentAddedEvent)

    }

}

fun onGameObjectComponentAdded(action: (GameObject, Component) -> Unit) = object : GameObjectComponentAddedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onGameObjectComponentAdded(event: GameObjectComponentAddedEvent) {
        action(event.gameObject,event.component)
    }
}