package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.events.Event

@Event("scene.created", [EditorScene::class])
class SceneCreatedEvent(val scene:EditorScene) {

    interface Listener {
        @Subscribe
        fun onSceneCreated(event: SceneCreatedEvent)

    }

}

fun onSceneCreated(action: (SceneCreatedEvent) -> Unit) = object : SceneCreatedEvent.Listener {

    init {
        Bus.register(this)
    }
    @Subscribe
    override fun onSceneCreated(event: SceneCreatedEvent) {
        action(event)
    }
}