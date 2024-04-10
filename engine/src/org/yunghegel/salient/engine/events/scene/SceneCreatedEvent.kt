package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class SceneCreatedEvent {

    interface Listener
    {
        @Subscribe
        fun onSceneCreated(event: SceneCreatedEvent)

    }

}

fun onSceneCreated(action: ()->Unit) = object : SceneCreatedEvent.Listener {

    init {
        Bus.register(this)
    }

    override fun onSceneCreated(event: SceneCreatedEvent) {
        action()
    }
}