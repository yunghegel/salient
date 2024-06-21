package org.yunghegel.gdx.utils

import org.yunghegel.gdx.utils.ext.observable

class IntervalAction(val interval: Float, val action: (Float) -> Unit) {

    init {
        timeTrackedActions.add(this)
    }

    private var accumulatedTime = 0f

    var disposable = false
    var numIterations by observable(0) { old,new ->
        println("IntervalAction: $old -> $new")
    }

    var disposeWhen : (Int)->Boolean = { false }
        set(value) {
            field = value
            disposable = true
        }


    /**
     * Call this method with the delta time (time since last update).
     * @param deltaTime Time in seconds since the last update call.
     */
    private fun update(deltaTime: Float) {
        accumulatedTime += deltaTime

        // Check if accumulated time exceeds or meets the interval
        if (accumulatedTime >= interval) {
            // Perform the action
            action(deltaTime)
            numIterations++

            // Reset the accumulated time, also account for the overflow
            accumulatedTime -= interval

            if (disposable && disposeWhen(numIterations)) {
                timeTrackedActions.remove(this)
            }
        }
    }

    fun disposeAfter(dispose: ()->Boolean) = apply { disposeWhen = { dispose() } }

    fun disposeAfter(dispose: (Int)->Boolean) = apply { disposeWhen = dispose }

    companion object Context {
        val timeTrackedActions = hashSetOf<IntervalAction>()

        fun update(deltaTime: Float) {
            timeTrackedActions.forEach { it.update(deltaTime) }
        }
    }

}

fun every(interval: Float, action: (Float) -> Unit) = IntervalAction(interval, action)