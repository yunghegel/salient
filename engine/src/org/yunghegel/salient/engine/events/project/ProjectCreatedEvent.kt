package org.yunghegel.salient.engine.events.project

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.events.Event

@Event("project.creatad", [EditorProject::class])
class ProjectCreatedEvent(val project:EditorProject<*,*>) {

    interface Listener {
        @Subscribe
        fun onProjectCreated(event: ProjectCreatedEvent)

    }

}

fun onProjectCreated(action: (ProjectCreatedEvent) -> Unit) = object : ProjectCreatedEvent.Listener {

    init {
        Bus.register(this)
    }
    @Subscribe
    override fun onProjectCreated(event: ProjectCreatedEvent) {
        action(event)
    }
}