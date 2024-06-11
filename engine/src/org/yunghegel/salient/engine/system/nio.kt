package org.yunghegel.salient.engine.system

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import ktx.async.newSingleThreadAsyncContext
import org.yunghegel.salient.engine.system.file.FileDaemon
import java.nio.file.*


@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
object nio {

    val async = newSingleThreadAsyncContext()

    val thread = newSingleThreadContext("nio")

    private var daemon : WatchService? = null

    var filedaemon : IODaemon? = null

    fun fileDaemon(rootpath: String) : WatchService {
        val fs = FileSystems.getDefault()
        daemon = fs.newWatchService()
        val path = Paths.get(rootpath)
        path.register(
            daemon,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_DELETE,
            StandardWatchEventKinds.ENTRY_MODIFY);
        registerChildren(path, daemon!!)
        return daemon!!
    }

    private fun registerChildren(path: Path, watcher: WatchService) {
        if (path.toFile().isDirectory) {
            path.register(
                watcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
            val children = path.toFile().listFiles()
            if (children != null) {
                for (child in children) {
                    registerChildren(child.toPath(), watcher)
                }
            }
        }
    }

    fun poll(watchService: WatchService = daemon!!, handler:FileDaemon) {
        while (true) {
            val key = watchService.take()
            for (event in key.pollEvents()) {
                val kind = event.kind()
                val watchable = key.watchable()
                handler.pollevents(kind, watchable)
            }
        }
    }

    fun WatchService.initializeService(handler : FileDaemon) : IODaemon {
        filedaemon = IODaemon()
        filedaemon!!.watchers.add(handler)
        filedaemon!!.poll()
        return filedaemon!!
    }

    class IODaemon() {
        val watchers = mutableListOf<FileDaemon>()

        fun watch (handler: FileDaemon) {
            watchers.add(handler)
        }

         internal fun poll() {
            while (true) {
                val key = daemon!!.take()
                for (event in key.pollEvents()) {
                    val kind = event.kind()
                    val watchable = key.watchable()
                    for (watcher in watchers) {
                        watcher.pollevents(kind, watchable)
                    }
                }
            }
        }

    }

}