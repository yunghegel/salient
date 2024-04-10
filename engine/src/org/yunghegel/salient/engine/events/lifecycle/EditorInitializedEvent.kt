package org.yunghegel.salient.engine.events.lifecycle

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class EditorInitializedEvent() {

    interface Listener {
        @Subscribe
        fun onEditorInitialized(event: EditorInitializedEvent)

    }

}

fun onEditorInitialized(action: () -> Unit) = object : EditorInitializedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onEditorInitialized(event: EditorInitializedEvent) {
        action()
    }
}