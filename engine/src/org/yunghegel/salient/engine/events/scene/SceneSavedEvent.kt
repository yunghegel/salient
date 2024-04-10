package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.events.Bus

class SceneSavedEvent(val scene: EditorScene) {

    interface Listener {
        @Subscribe
        fun onSceneSaved(event: SceneSavedEvent)

    }

}

fun onSceneSaved(action: () -> Unit) = object : SceneSavedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onSceneSaved(event: SceneSavedEvent) {
        action()
    }
}