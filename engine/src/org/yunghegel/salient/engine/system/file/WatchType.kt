package org.yunghegel.salient.engine.system.file

import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent

enum class WatchType {

    ENTRY_CREATE,
    ENTRY_DELETE,
    ENTRY_MODIFY,
    OVERFLOW;

    companion object {

        fun from (kind : WatchEvent.Kind<*>): WatchType {
            return when (kind) {
                StandardWatchEventKinds.ENTRY_CREATE -> ENTRY_CREATE
                StandardWatchEventKinds.ENTRY_DELETE -> ENTRY_DELETE
                StandardWatchEventKinds.ENTRY_MODIFY -> ENTRY_MODIFY
                StandardWatchEventKinds.OVERFLOW -> OVERFLOW
                else -> throw IllegalArgumentException("Unknown event kind: $kind")
            }
        }
    }

}