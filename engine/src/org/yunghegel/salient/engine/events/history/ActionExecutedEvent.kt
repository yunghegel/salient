package org.yunghegel.salient.engine.events.history

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class ActionExecutedEvent() {

    interface Listener
    {
        @Subscribe
        fun onActionExecuted(event: ActionExecutedEvent)

    }

}

fun onActionExecuted(action: ()->Unit) = object : ActionExecutedEvent.Listener {

    init {
        Bus.register(this)
    }
    
    @Subscribe
    override fun onActionExecuted(event: ActionExecutedEvent) {
        action()
    }
}