package org.yunghegel.salient.engine.events.project

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.events.Bus
import org.yunghegel.salient.engine.events.Event

@Event("project.saved", [EditorProject::class])
class ProjectSavedEvent(val project: EditorProject<*,*>) {

    interface Listener {
        @Subscribe
        fun onProjectSaved(event: ProjectSavedEvent)

    }

}

fun onProjectSaved(action: () -> Unit) = object : ProjectSavedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onProjectSaved(event: ProjectSavedEvent) {
        action()
    }
}