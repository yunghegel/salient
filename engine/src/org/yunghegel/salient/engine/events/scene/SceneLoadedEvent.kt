package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.events.Bus

class SceneLoadedEvent(val scene: EditorScene) {

    interface Listener {
        @Subscribe
        fun onSceneLoaded(event: SceneLoadedEvent)

    }

}

fun onSceneLoaded(action: (SceneLoadedEvent) -> Unit) = object : SceneLoadedEvent.Listener {

    init {
        Bus.register(this)
    }
    @Subscribe
    override fun onSceneLoaded(event: SceneLoadedEvent) {
        action(event)
    }
}