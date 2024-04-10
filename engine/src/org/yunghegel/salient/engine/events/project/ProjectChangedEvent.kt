package org.yunghegel.salient.engine.events.project

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.events.Bus

class ProjectChangedEvent(val old:EditorProject<*,*>?,val new : EditorProject<*,*>) {

    interface Listener {
        @Subscribe
        fun onProjectChanged(event: ProjectChangedEvent)

    }

}

fun onProjectChanged(action: (ProjectChangedEvent) -> Unit) = object : ProjectChangedEvent.Listener {

    init {
        Bus.register(this)
    }
    @Subscribe
    override fun onProjectChanged(event: ProjectChangedEvent) {
        action(event)
    }
}