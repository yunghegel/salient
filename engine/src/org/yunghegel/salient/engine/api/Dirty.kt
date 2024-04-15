package org.yunghegel.salient.engine.api

import org.yunghegel.salient.engine.system.info

interface Dirty<T:Dirty<T>> {

    var dirty : Boolean

    val listeners: MutableList<DirtyListener<T>>

    fun markDirty(dirty: Boolean = true) {
        this.dirty = dirty
        info("marked ${this::class.simpleName} as dirty")
        if (dirty) {
            onDirty()
            listeners.forEach { listener ->
                listener.onDirty(this)
            }
        }
        this.dirty = false
    }

    open fun onDirty() {}

}