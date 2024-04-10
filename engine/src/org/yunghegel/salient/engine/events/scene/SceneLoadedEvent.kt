package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class SceneLoadedEvent {

    interface Listener
    {
        @Subscribe
        fun onSceneLoaded(event: SceneLoadedEvent)

    }

}

fun onSceneLoaded(action: ()->Unit) = object : SceneLoadedEvent.Listener {

    init {
        Bus.register(this)
    }

    override fun onSceneLoaded(event: SceneLoadedEvent) {
        action()
    }
}