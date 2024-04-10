package org.yunghegel.salient.editor.plugins.events

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class ToolLoadedEvent() {

    interface Listener
    {
        @Subscribe
        fun onToolLoaded(event: ToolLoadedEvent)

    }

}

fun onToolLoaded(action: ()->Unit) = object : ToolLoadedEvent.Listener {

    init {
        Bus.register(this)
    }
    
    @Subscribe
    override fun onToolLoaded(event: ToolLoadedEvent) {
        action()
    }
}