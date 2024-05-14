package org.yunghegel.salient.engine.system.perf

import com.badlogic.gdx.utils.PerformanceCounter
import org.yunghegel.gdx.utils.ext.delta

object ProfilingConfig {
    var enabled = false
}

class Profile(val name: String, val invocations: Int = 100, var autorestart: Boolean = false ) {

    val perf = PerformanceCounter(name)
    var count = 0

    fun start() {
        perf.reset()
        perf.start()
    }

    fun end() {
        perf.stop()
        perf.tick(delta)
    }

    fun print() {

        println("Profile: $name")
        println("Average time: ${perf.time.average} ms")
        println("Max time: ${perf.time.max} ms")
        println("Min time: ${perf.time.min} ms")
        println("Total time: ${perf.time.total} ms")
        println("Invocations: $invocations")
    }

    fun profile(block: () -> Unit) {
        if (!ProfilingConfig.enabled) {
            block()
            return
        }
        if (count < invocations) {
            start()
            block()
            end()
            count++
        } else {
            if (autorestart) {
                count = 0
            }
            block()
        }
    }

}


fun profile(name: String, invocations: Int = 100, autorestart: Boolean = false, block: () -> Unit) {
    val profile = Profile(name, invocations, autorestart)
    profile.profile(block)
    profile.print()
}