package org.yunghegel.salient.engine.events.history

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.undo.Action
import org.yunghegel.salient.engine.events.Bus

class ActionExecutedEvent(val action: Action) {

    interface Listener {
        @Subscribe
        fun onActionExecuted(event: ActionExecutedEvent)

    }

}

fun onActionExecuted(action: (ActionExecutedEvent) -> Unit) = object : ActionExecutedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onActionExecuted(event: ActionExecutedEvent) {
        action(event)
    }
}