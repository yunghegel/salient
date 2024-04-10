package org.yunghegel.salient.engine.events.project

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class ProjectLoadedEvent {

    interface Listener
    {
        @Subscribe
        fun onProjectLoaded(event: ProjectLoadedEvent)

    }

}

fun onProjectLoaded(action: ()->Unit) = object : ProjectLoadedEvent.Listener {

    init {
        Bus.register(this)
    }

    override fun onProjectLoaded(event: ProjectLoadedEvent) {
        action()
    }
}