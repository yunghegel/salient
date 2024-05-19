package org.yunghegel.salient.engine.system.perf

import com.badlogic.gdx.utils.PerformanceCounter
import com.badlogic.gdx.utils.StringBuilder
import org.yunghegel.gdx.utils.ext.delta
import org.yunghegel.gdx.utils.ext.floatToString


object ProfilingConfig {
    var enabled = false
}

class Profile(val name: String, val invocations: Int = 100, var autorestart: Boolean = false ) {

    val perf = PerformanceCounter(name)
    var count = 0
    var total = 0f

    fun start() {
        perf.reset()
        perf.start()
    }

    fun end() {
        perf.stop()
        perf.tick(delta)
    }

    fun print() : String {
        val sb = StringBuilder()
        val results = "${perf.toString(sb)}"
        return results
    }

    fun profile(block: () -> Unit) {
        if (!ProfilingConfig.enabled) {
            block()
            return
        }

        if (invocations==-1) {
            start()
            block()
            end()
            total++
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
            total++
            block()
        }
    }

}

data class ProfileResult(val name: String = "Profile", val shortest: Float=0f, val longest: Float=0f, val average: Float=0f, val invocations: Int=0, val total: Int=0) {

    fun convertToMs() : ProfileResult {
        return ProfileResult(name, shortest/1000, longest/1000, average/1000, invocations, total)
    }

    override fun toString(): String {
        return "$name[min:${floatToString(shortest)}|max:${floatToString(longest,2)}|avg:${floatToString(average,2)}]"
    }
}


fun profile(name: String, invocations: Int = 100, autorestart: Boolean = false, block: () -> Unit) : ProfileResult {
    val profile = Profile(name, invocations, autorestart)
    profile.profile(block)
    return ProfileResult(name, profile.perf.time.min, profile.perf.time.max, profile.perf.time.average, profile.count,profile.total.toInt()).convertToMs()
}