package org.yunghegel.salient.editor.plugins.events

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.editor.plugins.PluginModule
import org.yunghegel.salient.engine.events.Bus

class PluginModuleLoadedEvent(val module : PluginModule) {

    interface Listener {
        @Subscribe
        fun onPluginModuleLoaded(event: PluginModuleLoadedEvent)

    }

}

fun onPluginModuleLoaded(action: (PluginModuleLoadedEvent) -> Unit) = object : PluginModuleLoadedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onPluginModuleLoaded(event: PluginModuleLoadedEvent) {
        action(event)
    }
}