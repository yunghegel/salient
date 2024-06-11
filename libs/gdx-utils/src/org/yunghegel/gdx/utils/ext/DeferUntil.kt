package org.yunghegel.gdx.utils.ext

import java.util.Stack

data class Deferred(val action: ()->Unit, val condition: ()->Boolean = { true })

interface Deferrable {
    val deferred: Stack<Deferred>
    fun defer(action: ()->Unit, condition: ()->Boolean) {
        deferred.add(Deferred(action, condition))
    }
    fun defer(action: ()->Unit) {
        deferred.add(Deferred(action, {true}))
    }
    fun deferUntil(condition: ()->Boolean, action: ()->Unit) {
        deferred.add(Deferred(action, condition))
    }
    fun deferUntil(condition: ()->Boolean) {
        deferred.add(Deferred({}, condition))
    }
    fun execDeferred() {
        while (deferred.isNotEmpty()) {
            val d = deferred.pop()
            if (d.condition()) {
                d.action()
            } else {
                deferred.add(d)
                break
            }
        }
    }
}
