package org.yunghegel.salient.engine.events.project

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class ProjectSavedEvent() {

    interface Listener
    {
        @Subscribe
        fun onProjectSaved(event: ProjectSavedEvent)

    }

}

fun onProjectSaved(action: ()->Unit) = object : ProjectSavedEvent.Listener {

    init {
        Bus.register(this)
    }
    
    @Subscribe
    override fun onProjectSaved(event: ProjectSavedEvent) {
        action()
    }
}