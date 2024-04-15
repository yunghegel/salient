package org.yunghegel.salient.engine.events.scene

import com.badlogic.ashley.core.Component
import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.graphics.scene3d.GameObject

class GameObjectComponentRemovedEvent(val gameObject: GameObject, val component: Component) {

    interface Listener {
        @Subscribe
        fun onGameObjectComponentRemoved(event: GameObjectComponentRemovedEvent)

    }

}

fun onGameObjectComponentRemoved(action: (GameObject,Component) -> Unit) = object : GameObjectComponentRemovedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onGameObjectComponentRemoved(event: GameObjectComponentRemovedEvent) {
        action(event.gameObject,event.component)
    }
}