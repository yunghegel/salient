package org.yunghegel.salient.engine.events

import com.badlogic.gdx.files.FileHandle
import org.greenrobot.eventbus.Logger
import org.yunghegel.salient.engine.events.lifecycle.onShutdown
import org.yunghegel.salient.engine.system.debug
import org.yunghegel.salient.engine.system.file.Paths
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.system.warn
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level

const val logFilePath : String = "log.txt"

class EventLog : Logger {
    var maxLines = 2000
    val stack = Stack<EventLog>()
    val logFile: FileHandle = Paths.SALIENT_HOME.child(logFilePath).handle
    val logFileWriter: PrintWriter = PrintWriter(logFile.writer(true))

    init {
        onShutdown {
            logFileWriter.close()
        }
    }

    override fun log(level: Level, msg: String?) {
        val log = EventLog(level!!, msg!!)
        stack.push(log)
        if (stack.size > maxLines) {
            stack.removeAt(0)
        }
        when(level) {
            Level.SEVERE -> warn(msg)
            Level.WARNING -> warn(msg)
            Level.INFO -> info(msg)
            Level.CONFIG -> debug(msg)
            Level.FINE -> debug(msg)
        }

        logFileWriter.println(stack.peek().toString())
        logFileWriter.flush()
    }

    override fun log(level: Level, msg: String?, th: Throwable?) {
        log(level, msg)
    }

    data class EventLog(val level: Level, val msg: String) {

        override fun toString(): String {
            return "${time()} $level: $msg"
        }

        fun time(): String {
            return (SimpleDateFormat("HH:mm:ss").format(Date()).toString())
        }
    }


}