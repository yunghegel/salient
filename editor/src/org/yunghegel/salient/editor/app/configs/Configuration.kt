package org.yunghegel.salient.editor.app.configs

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

typealias SyncAction<T> = () -> T

@Serializable
abstract class Configuration {

    @Transient
    var sync_actions = mutableListOf<SyncAction<*>>()

    fun registerSyncAction(action: SyncAction<*>) {
        sync_actions.add(action)
    }

    fun sync() {
        sync_actions.forEach { it.invoke() }
    }



}