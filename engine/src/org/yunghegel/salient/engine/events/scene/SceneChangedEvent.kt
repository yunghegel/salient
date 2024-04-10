package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.events.Bus

class SceneInitializedEvent(val scene: EditorScene) {

    interface Listener {
        @Subscribe
        fun onSceneInitialized(event: SceneInitializedEvent)

    }

}

fun onSceneInitialized(action: () -> Unit) = object : SceneInitializedEvent.Listener {

    init {
        Bus.register(this)
    }
    @Subscribe
    override fun onSceneInitialized(event: SceneInitializedEvent) {
        action()
    }
}