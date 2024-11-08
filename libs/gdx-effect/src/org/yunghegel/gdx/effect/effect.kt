package org.yunghegel.gdx.effect


import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.onEach
import ktx.async.KtxAsync
import org.yunghegel.gdx.effect.event.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.coroutines.CoroutineContext
import kotlin.properties.Delegates
import kotlin.reflect.KClass

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
private object state {

    var finalized = false
    var asyncExecutor : CoroutineContext = newSingleThreadContext("EventBus")
    var processEvents = true

    var lock : ReentrantLock by lazyMutable { ReentrantLock() }
    var channel : Channel<Event> by lazyMutable { Channel() }


    var renderingThread : Thread? = null
    var flow = MutableSharedFlow<Event>()

    fun init() {
        asyncExecutor = newSingleThreadContext("EventBus")
    }
}

object EventBus : CoroutineScope by CoroutineScope(state.asyncExecutor) {

    val subscriberMap = mutableMapOf<KClass<out Event>, MutableList<EventSubscription>>()

    val consumer : (suspend (Event)->Unit) = { event ->
        async { }
    }

    fun initiate() {
        KtxAsync.initiate()

        state.flow.onEach { event ->
            println(event)
            subscriberMap[event::class]?.forEach { sub ->
                println(event::class.simpleName)
                if (sub.async) {
                    val deffered = async {
                        sub.asyncExecutor?.invoke(event)
                    }
                    deffered.await()?.let { sub.consumer(it) }

                } else {
                    sub.onEvent(event)
                }
            }
        }
        state.lock = ReentrantLock()

        launch(state.asyncExecutor) {
            state.flow.collect { event ->
                subscriberMap[event::class]?.forEach { sub ->
                    println("found subscription")
                    if (sub.async) {
//                        val deffered = async {
//                            sub.asyncExecutor?.invoke(event)
//                        }
//                        deffered.await()?.let { sub.consumer(it) }
                        launch { sub.onEvent(event) }

                    } else {
                        sub.apply {
                            listener?.let { it(event) }
                        }
                    }
                }
            }
        }



    }

    fun <T: KClass<out E>, E:Event> subscribe(event:T, consumer: EventConsumer, asyncExecutor: AsyncEventListener? = null, listener: EventListener? = null) {
        if (subscriberMap[event]==null) {
            subscriberMap[event] = mutableListOf()
        }

        subscriberMap[event]?.add(EventSubscription(listener,asyncExecutor,consumer))

        try {
            state.lock.lock()
        } finally {
            state.lock.unlock()

        }
    }

    fun register(event: Class<out Event>) {
        state.lock.lock()
        try {
            subscriberMap[event.kotlin] = mutableListOf()
        } finally {
            state.lock.unlock()
        }
    }

    fun <T:Event> post(event: T) {
//        state.lock.lock()
        launch {
            state.flow.emit(event)
        }


    }

    fun <T:Event> handle(event:T, subscription: EventSubscription) {
        state.flow.tryEmit(event)
    }

    private fun onRenderingThread(action: ()->Unit) {
        state.lock.lock()
        try {
            action()
        } finally {
            state.lock.unlock()
        }
    }
}



fun <T> lock(block: () -> T): T {
    state.lock.lock()
    try {
        return block()
    } finally {
        state.lock.unlock()
    }
}

fun <T> lock(block: () -> T, onRenderingThread: (T) -> Unit) {
    state.lock.lock()
    try {
        val result = block()
        onRenderingThread(result)
    } finally {
        state.lock.unlock()
    }
}