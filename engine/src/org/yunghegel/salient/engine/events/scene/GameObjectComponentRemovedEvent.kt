package org.yunghegel.salient.engine.events.scene

import com.badlogic.ashley.core.Component
import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.events.Event
import org.yunghegel.salient.engine.scene3d.GameObject

@Event("gameobject.component.removed", [GameObject::class, Component::class])
class GameObjectComponentRemovedEvent(val gameObject: GameObject, val component: Component) {

    interface Listener {
        @Subscribe
        fun onGameObjectComponentRemoved(event: GameObjectComponentRemovedEvent)

    }

}

fun onGameObjectComponentRemoved(action: (GameObject, Component) -> Unit) = object : GameObjectComponentRemovedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onGameObjectComponentRemoved(event: GameObjectComponentRemovedEvent) {
        action(event.gameObject,event.component)
    }
}