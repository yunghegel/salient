package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class GameObjectSelectedEvent() {

    interface Listener
    {
        @Subscribe
        fun onGameObjectSelected(event: GameObjectSelectedEvent)

    }

}

fun onGameObjectSelected(action: ()->Unit) = object : GameObjectSelectedEvent.Listener {

    init {
        Bus.register(this)
    }
    
    @Subscribe
    override fun onGameObjectSelected(event: GameObjectSelectedEvent) {
        action()
    }
}