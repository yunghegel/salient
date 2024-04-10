package org.yunghegel.salient.engine.io

import com.badlogic.gdx.graphics.Color
import org.yunghegel.gdx.utils.ext.Ansi
import org.yunghegel.gdx.utils.ext.colorize

import org.yunghegel.salient.engine.events.lifecycle.onShutdown
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*

object Log {

    val logFolderPath = Paths.SALIENT_HOME.path + "/logs"

    val folder = File(logFolderPath)

    val stack = Stack<LogReference>()

    val handlers = mutableListOf<LogHandler>()

    var logWriter: PrintWriter? = null

    init {
        initFile()
        addHandler { logRef ->
            logWriter?.println(logRef.format())
            logWriter?.flush()
        }

        onShutdown {
            logWriter?.println("Log file closed at ${Date()}")
            logWriter?.println("---------------------------------")

            logWriter?.flush()
            logWriter?.close()
        }
    }

    fun addHandler(handler: LogHandler) {
        handlers.add(handler)
    }

    fun initFile() {
        val fileDateFormat = SimpleDateFormat("yy-MM-dd")
        val fileName = fileDateFormat.format(Date())

        val dir = folder.mkdir()
        if (!dir) {
        }

        try {
            val file = File("$logFolderPath/salient_${fileName}.log")
            if (!file.exists()) {
                file.createNewFile()
            }
            logWriter = PrintWriter(FileWriter(file, true))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        logWriter?.println("Salient log file created at ${Date()}")
    }

    fun log(msg: String, level: LogLevel = LogLevel.DEBUG) {
        val info = getCallerInfo()
        val time = getTime()
        val logRef = LogReference(info.first, info.second, info.third, time, level, msg)
        println(logRef.formatColored())
        stack.push(logRef)
        handle(logRef)
    }

    fun info(msg: String) {
        log(msg, LogLevel.INFO)
    }

    fun handle(logRef: LogReference) {
        handlers.forEach { it(logRef) }
    }

    fun processStack(handler: LogHandler) {
        while (!stack.isEmpty()) {
            handler(stack.pop())
        }
    }

    fun getCallerInfo(): Triple<String, String, Int> {
        val st = getCallingStackTraceElement(Log::class.java)
        val classname = st?.className
        val trimmedClassname = classname?.substring(classname.lastIndexOf('.') + 1)
        val methodname = st?.methodName
        val line = st?.lineNumber
        return Triple(trimmedClassname!!, methodname!!, line!!)
    }

    fun getTime(): String {
        return SimpleDateFormat("HH:mm:ss").format(Date())
    }

    fun ansify(msg: String, level: LogLevel): String {
        return when (level) {
            LogLevel.INFO -> colorize(msg, Ansi.WHITE)
            LogLevel.DEBUG -> colorize(msg, Ansi.BLUE)
            LogLevel.ERROR -> colorize(msg, Ansi.RED)
            LogLevel.WARN -> colorize(msg, Ansi.PURPLE)
            LogLevel.STATE -> colorize(msg, Ansi.YELLOW)
            LogLevel.EVENT -> colorize(msg, Ansi.GREEN)
        }
    }

}

fun log(msg: String) {
    Log.log(msg)
}

fun info(msg: String) {
    Log.log(msg, LogLevel.INFO)
}

fun debug(msg: String) {
    Log.log(msg, LogLevel.DEBUG)
}

fun error(msg: String) {
    Log.log(msg, LogLevel.ERROR)
}

fun error(msg:String, triggerDialog: Boolean = false,createDialog: (()->Unit) = { }) {
    Log.log(msg, LogLevel.ERROR)
    if(triggerDialog) {
        val dialog = createDialog.invoke()
    }
}

fun event(type: Class<*>) {
    Log.log(type.simpleName, LogLevel.EVENT)
}


fun warn(msg: String) {
    Log.log(msg, LogLevel.WARN)
}



typealias LogHandler = (LogReference) -> Unit

enum class LogLevel(val color: Color) {
    INFO(Color.WHITE), DEBUG(Color.CYAN), WARN(Color.CORAL), ERROR(Color.FIREBRICK), STATE(Color.GOLD), EVENT(Color.FOREST)
}

data class LogReference(
    val className: String,
    val methodName: String,
    val line: Int,
    val time: String,
    val level: LogLevel,
    val msg: String
) {

    fun format(): String {
        return "$time [$level] [$className.$methodName:$line]\n$msg"
    }

    fun formatColored(): String {
        return when (level) {
            LogLevel.INFO -> "$time ${Log.ansify(level.name, level)} $className.$methodName:$line - $msg"
            LogLevel.DEBUG -> "$time ${Log.ansify(level.name, level)} $className.$methodName:$line - $msg"
            LogLevel.ERROR -> "$time ${Log.ansify(level.name, level)} $className.$methodName:$line - $msg"
            LogLevel.WARN -> "$time ${Log.ansify(level.name, level)} $className.$methodName:$line - $msg"
            LogLevel.STATE -> "$time ${Log.ansify(level.name, level)} $className.$methodName:$line - $msg"
            LogLevel.EVENT -> "${Log.ansify(msg, level)} [$className.$methodName:$line]"
        }
    }

}

fun getCallingStackTraceElement(aclass: Class<*>?): StackTraceElement? {
    val t = Throwable()
    val ste = t.stackTrace
    var index = 3
    val limit = ste.size - 2
    var st = ste[index]
    var className = st.className
    var aclassfound = aclass == null
    var resst: StackTraceElement? = null
    while (index < limit) {
        if (shouldExamine(className, aclass)) {
            if (resst == null) {
                resst = st
            }
            if (aclassfound) {
                val ast = onClassfound(aclass, className, st)
                if (ast != null) {
                    resst = ast
                    break
                }
            } else {
                if (aclass != null && aclass.name == className) {
                    aclassfound = true
                }
            }
        }
        index = index + 1
        st = ste[index]
        className = st.className
    }
    if (resst == null) {
        //Assert.isNotNull(resst, "stack trace should null"); //NO OTHERWISE circular dependencies
        System.err.println("StacktraceElement not found") //$NON-NLS-1$
    }
    return resst
}

private fun shouldExamine(className: String, aclass: Class<*>?): Boolean {
    val res = Log::class.java.getName() != className && (!className.endsWith(
        "LogKt"
    ) && (!className.contains("Event") && (!className.contains("events")) && (!className.contains("Logger")) || (aclass != null && aclass.name.endsWith(
        "LogUtils"
    )) || (aclass == null && !className.startsWith("Log"))))
    return res
}

private fun onClassfound(aclass: Class<*>?, className: String, st: StackTraceElement): StackTraceElement? {
    var resst: StackTraceElement? = null
    if (aclass != null && aclass.name != className) {
        resst = st
    }
    if (aclass == null) {
        resst = st
    }
    return resst
}