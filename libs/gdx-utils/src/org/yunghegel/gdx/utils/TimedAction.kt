package org.yunghegel.gdx.utils

class TimedAction(val action: () -> Unit, val delay: Float, val repeat: Boolean = true,var time: Float = 0f) {

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

fun temporalAction(action: () -> Unit, delay: Float, repeat: Boolean = true) = TimedAction(action, delay, repeat)