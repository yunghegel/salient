package org.yunghegel.salient.engine.events.project

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class ProjectChangedEvent {

    interface Listener
    {
        @Subscribe
        fun onProjectChanged(event: ProjectChangedEvent)

    }

}

fun onProjectChanged(action: ()->Unit) = object : ProjectChangedEvent.Listener {

    init {
        Bus.register(this)
    }

    override fun onProjectChanged(event: ProjectChangedEvent) {
        action()
    }
}