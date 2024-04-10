package org.yunghegel.salient.engine.api

interface Dirty {

    var dirty : Boolean

    fun markDirty(dirty: Boolean) {
        this.dirty = dirty
        if (dirty) onDirty()
        this.dirty = false
    }

    fun onDirty()

}