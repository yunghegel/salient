package org.yunghegel.salient.engine.events.project

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.events.Bus

class ProjectLoadedEvent(val project: EditorProject<*,*>?) {

    interface Listener {
        @Subscribe
        fun onProjectLoaded(event: ProjectLoadedEvent)

    }

}

fun onProjectLoaded(action: (ProjectLoadedEvent) -> Unit) = object : ProjectLoadedEvent.Listener {

    init {
        Bus.register(this)
    }
    @Subscribe
    override fun onProjectLoaded(event: ProjectLoadedEvent) {
        action(event)
    }
}