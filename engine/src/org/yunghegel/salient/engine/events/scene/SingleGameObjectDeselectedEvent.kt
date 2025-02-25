package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.events.Event
import org.yunghegel.salient.engine.scene3d.GameObject

@Event("scene.singleGameObjectDeselected", [GameObject::class])
class SingleGameObjectDeselectedEvent(val go: GameObject) {

    interface Listener {
        @Subscribe
        fun onSingleGameObjectDeselected(event: SingleGameObjectDeselectedEvent)

    }

}

fun onSingleGameObjectDeselected(action: (GameObject) -> Unit) = object : SingleGameObjectDeselectedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onSingleGameObjectDeselected(event: SingleGameObjectDeselectedEvent) {
        action(event.go)
    }
}