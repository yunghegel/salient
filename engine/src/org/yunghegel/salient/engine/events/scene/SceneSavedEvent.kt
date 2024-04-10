package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class SceneSavedEvent {

    interface Listener
    {
        @Subscribe
        fun onSceneSaved(event: SceneSavedEvent)

    }

}

fun onSceneSaved(action: ()->Unit) = object : SceneSavedEvent.Listener {

    init {
        Bus.register(this)
    }

    override fun onSceneSaved(event: SceneSavedEvent) {
        action()
    }
}