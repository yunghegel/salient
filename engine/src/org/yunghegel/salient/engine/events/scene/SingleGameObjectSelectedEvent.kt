package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.events.Event
import org.yunghegel.salient.engine.scene3d.GameObject

@Event("scene.singleGameObjectSelected", [GameObject::class])
class SingleGameObjectSelectedEvent(val go: GameObject) {

    interface Listener {
        @Subscribe
        fun onSingleGameObjectSelected(event: SingleGameObjectSelectedEvent)

    }

}

fun onSingleGameObjectSelected(action: (GameObject) -> Unit) = object : SingleGameObjectSelectedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onSingleGameObjectSelected(event: SingleGameObjectSelectedEvent) {
        action(event.go)
    }
}