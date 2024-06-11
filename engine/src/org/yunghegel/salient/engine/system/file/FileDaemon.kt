package org.yunghegel.salient.engine.system.file

import java.nio.file.WatchEvent
import java.nio.file.Watchable

fun interface FileDaemon {

     fun pollevents(kind: WatchEvent.Kind<out Any>, watchable: Watchable)

}