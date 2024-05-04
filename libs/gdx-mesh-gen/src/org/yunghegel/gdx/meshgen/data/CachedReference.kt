package org.yunghegel.gdx.meshgen.data

import mobx.core.autorun

interface CachedReference<R:DirtySyncronized,T> {

    val syncronizer : DirtySyncronizer<R, T>

    val ref : R

    var cached : T

    val listeners : MutableList<DirtyListener<T>>

    fun listen(fn: (T)->Unit) {
        autorun {
            fn(cached)
        }
    }

     fun store(value: T) {
        cached = value
    }

    fun retrieve(): T {
        var cached : T = this.cached
        if (checkDirty()) {
            cached = resolveValue()
            store(cached)
        }
        return cached
    }

     fun resolveValue() : T {
       val current = syncronizer.getCurrent(ref)
        ref.setClean()
        return current
    }

    fun checkDirty(): Boolean {
        return ref.isDirty()
    }

    operator fun getValue(thisRef: Any?, property: Any?): T {
        return retrieve()
    }

}

fun interface DirtySyncronizer<Ref:DirtySyncronized,Type> {
    fun getCurrent(ref: Ref) : Type
}

fun interface DirtyListener<T> {
    fun onDirty(value: T)
}