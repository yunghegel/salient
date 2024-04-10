package org.yunghegel.salient.engine.events.scene

import org.greenrobot.eventbus.Subscribe
import org.yunghegel.salient.engine.events.Bus

class GameObjectDeselectedEvent() {

    interface Listener
    {
        @Subscribe
        fun onGameObjectDeselected(event: GameObjectDeselectedEvent)

    }

}

fun onGameObjectDeselected(action: ()->Unit) = object : GameObjectDeselectedEvent.Listener {

    init {
        Bus.register(this)
    }
    
    @Subscribe
    override fun onGameObjectDeselected(event: GameObjectDeselectedEvent) {
        action()
    }
}