package org.yunghegel.gdx.utils

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class TimedAction(
    val action: () -> Unit,
    val delay: Float = 1 / 60f,
    val repeat: Boolean = true,
    var time: Float = 0f
) {

    init {
        actionList.add(this)
    }

    fun update(delta: Float) {
        time += delta
        if (time >= delay) {
            action()
            if (repeat) time = 0f
        }

    }

    fun reset() {
        time = 0f
    }

    fun stop() {
        time = delay
    }

    fun isRunning() = time < delay

    companion object {
        val actionList = mutableListOf<TimedAction>()

        init {

        }

        fun updateAll(delta: Float) {
            actionList.forEach {
                it.update(delta);
                if (!it.repeat && it.time >= it.delay) {
                    actionList.remove(it)
                }

            }
        }

    }

}

fun temporal(delay: Float = 0f, repeat: Boolean = true, action: () -> Unit) = TimedAction(action, delay, repeat)


class TemporalDelgate(val delay: Float, val repeat: Boolean = true) : ReadWriteProperty<Any, () -> Unit> {
    private var action: () -> Unit = {}
    private var time = 0f

    override fun getValue(thisRef: Any, property: KProperty<*>): () -> Unit = action

    override fun setValue(thisRef: Any, property: KProperty<*>, value: () -> Unit) {
        action = value
    }

    fun update(delta: Float) {
        time += delta
        if(time >= delay) {
            action()
            if(repeat) time = 0f
        }
    }

    fun reset() {
        time = 0f
    }

    fun stop() {
        time = delay
    }

    fun isRunning() = time < delay

}

fun scheduled(delay: Float, repeat: Boolean = true) = TemporalDelgate(delay, repeat)