package org.yunghegel.salient.engine.events

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.model.ProjectHandle
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.events.Bus

class ProjectDiscoveryEvent(val project:ProjectHandle) {

    interface Listener {

        fun onProjectDiscovery(event: ProjectDiscoveryEvent)

    }

}

fun onProjectDiscovery(action: (ProjectDiscoveryEvent) -> Unit) = object : ProjectDiscoveryEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onProjectDiscovery(event: ProjectDiscoveryEvent) {
        action(event)
    }
}