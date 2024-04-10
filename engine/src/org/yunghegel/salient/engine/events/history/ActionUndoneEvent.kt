package org.yunghegel.salient.engine.events.history

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.api.undo.Action
import org.yunghegel.salient.engine.events.Bus

class ActionUndoneEvent(val action: Action) {

    interface Listener {
        @Subscribe
        fun onActionUndone(event: ActionUndoneEvent)

    }

}

fun onActionUndone(action: (ActionUndoneEvent) -> Unit) = object : ActionUndoneEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onActionUndone(event: ActionUndoneEvent) {
        action(event)
    }
}