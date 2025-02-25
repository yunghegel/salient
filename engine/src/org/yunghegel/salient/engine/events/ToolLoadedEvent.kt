package org.yunghegel.salient.engine.events

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.tool.InputTool

class ToolLoadedEvent(val tool: InputTool) {

    interface Listener {
        @Subscribe
        fun onToolLoaded(event: ToolLoadedEvent)

    }

}

fun onToolLoaded(action: (ToolLoadedEvent) -> Unit) = object : ToolLoadedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onToolLoaded(event: ToolLoadedEvent) {
        action(event)
    }
}