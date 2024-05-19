package org.yunghegel.salient.engine.events.lifecycle

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class FilesDroppedEvent(val paths: Array<out String>) {

    interface Listener {
        @Subscribe
        fun onFilesDropped(event: FilesDroppedEvent)

    }

}

fun filesDropped(action: (Array<out String>) -> Unit) = object : FilesDroppedEvent.Listener {

    init {
        Bus.register(this)
    }

    @Subscribe
    override fun onFilesDropped(event: FilesDroppedEvent) {
        action(event.paths)
    }
}