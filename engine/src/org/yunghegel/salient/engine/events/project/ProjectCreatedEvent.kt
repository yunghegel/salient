package org.yunghegel.salient.engine.events.project

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class ProjectCreatedEvent {

    interface Listener
    {
        @Subscribe
        fun onProjectCreated(event: ProjectCreatedEvent)

    }

}

fun onProjectCreated(action: ()->Unit) = object : ProjectCreatedEvent.Listener {

    init {
        Bus.register(this)
    }

    override fun onProjectCreated(event: ProjectCreatedEvent) {
        action()
    }
}