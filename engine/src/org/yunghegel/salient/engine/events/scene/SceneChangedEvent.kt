package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.events.Bus

class SceneChangedEvent(val old: EditorScene?, val new: EditorScene) {

    interface Listener {
        @Subscribe
        fun onSceneChanged(event: SceneChangedEvent)

    }

}

fun onSceneChanged(action: (SceneChangedEvent) -> Unit) = object : SceneChangedEvent.Listener {

    init {
        Bus.register(this)
    }
    @Subscribe
    override fun onSceneChanged(event: SceneChangedEvent) {
        action(event)
    }
}