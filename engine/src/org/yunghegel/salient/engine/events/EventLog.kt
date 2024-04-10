package org.yunghegel.salient.engine.events

import com.badlogic.gdx.files.FileHandle
import org.greenrobot.eventbus.Logger
import org.yunghegel.salient.engine.io.Paths
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level

const val logFilePath : String = "log.txt"

class EventLog : Logger {
    var maxLines = 2000
    val stack = Stack<EventLog>()
    val logFile: FileHandle = Paths.SALIENT_HOME.child(logFilePath).handle
    val logFileWriter: Writer = logFile.writer(true)

    override fun log(level: Level?, msg: String?) {
        val log = EventLog(level!!, msg!!)
        stack.push(log)
        if (stack.size > maxLines) {
            stack.removeAt(0)
        }
        println(log.toString())

        logFileWriter.write(stack.peek().toString())
        logFileWriter.flush()
    }



    override fun log(level: Level?, msg: String?, th: Throwable?) {
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