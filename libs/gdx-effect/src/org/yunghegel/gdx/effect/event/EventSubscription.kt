package org.yunghegel.gdx.effect.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.yunghegel.gdx.effect.EventBus

class EventSubscription(val listener: EventListener? = null, val asyncExecutor: AsyncEventListener? = null, val consumer: EventConsumer) {

    var async = asyncExecutor != null

    val payload: (Array<Any>) -> EventPayload = { args ->
        EventPayload(args)
    }

    private fun onAsyncEvent(event: Event,scope: CoroutineScope,asyncFn: AsyncEventListener) = scope.launch {
        val deffered = async { asyncFn(event) }
        consumer(deffered.await())
    }

    private fun onEvent(event: Event,syncFn: EventListener) {
//        consumer(syncFn(event))
    }

    fun onEvent(event: Event) {
        if (async) {
            onAsyncEvent(event, EventBus, asyncExecutor!!)
        } else {
            onEvent(event, listener!!)
        }
    }

}

inline fun <reified T: Event> listen(noinline listener: (T)->Unit, noinline consumer:(EventPayload)->Unit) {
    EventBus.subscribe(T::class, consumer,listener = listener as EventListener)
}
