package org.yunghegel.salient.engine.api

fun interface DirtyListener<T:Dirty<T>> {

    fun onDirty(dirty: Dirty<T>)

}