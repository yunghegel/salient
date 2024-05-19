package org.yunghegel.salient.launcher.exe

import com.badlogic.gdx.utils.PerformanceCounter
import com.badlogic.gdx.utils.StringBuilder
import ktx.app.profile

@JvmInline
value class Profile(val perf:PerformanceCounter) {
    fun profile(block:()->Unit) {
        perf.profile(0) {
            block()
        }
    }
    fun readableString() : String{
        val sb = StringBuilder()
        val results = "${perf.toString(sb)}"
        return results
    }
}

private object cache {
    val map = mutableMapOf<String, Profile>()
}

fun profile(name:String, block:()->Unit) : String {
    val perf = cache.map.getOrPut(name) {
        Profile(PerformanceCounter(name))
    }
    perf.profile(block)

    return perf.readableString()
}