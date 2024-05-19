package org.yunghegel.gdx.effect.event

class EventPayload constructor(vararg var args: Any) {

    var payload: Any = Any()
    var map = mutableMapOf<Int, Any>()

    init {
        for (i in args.indices) {
            map[i] = args[i]
        }
    }

    fun get(index: Int): Any {
        return map[index] ?: args[index]
    }

    fun set(index: Int, value: Any) {
        map[index] = value
    }

    inline fun <reified T> getValue(index: Int): T {
        return get(index) as T
    }

}