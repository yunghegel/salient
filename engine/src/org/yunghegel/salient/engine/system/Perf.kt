package org.yunghegel.salient.engine.system

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import org.yunghegel.salient.engine.events.lifecycle.onEditorInitialized
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter

object Perf {
    private var total: Long = 0

    var fileWriter: FileWriter? = null
    var writer: PrintWriter? = null

    init {
        try {
            fileWriter = FileWriter("profiler.txt")
            fileWriter!!.flush()
            fileWriter!!.close()
            fileWriter = FileWriter("profiler.txt", true)

            writer = PrintWriter(fileWriter)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        onEditorInitialized {
            flush()
        }

    }


    var metrics: ObjectMap<String, Metric> = ObjectMap()
    private val names = Array<String>()
    private val listeners = Array<PerfEndedListener>()

    fun start(name: String): Int {
        var m = metrics.get(name)
        if (m != null) {
            if (!m.started) {
                m.started = true
                m.startTime = time
            }
            return m.index
        } else {
            m = Metric()
            m.index = names.size
            names.add(name)
            metrics.put(name, m)
            m.started = true
            m.startTime = time
            return m.index
        }
    }

    fun end(index: Int) {
        val m = metrics.get(names[index])
        if (m.started) {
            m.duration = time - m.startTime
            m.started = false
        }
        for (listener in listeners) {
            listener.perfEnded(m, names[index])
        }
        writer!!.println(names[index] + ": " + m.duration)
        writer!!.flush()
        total += m.duration
    }

    fun flush(index: Int) {
        val name = names[index]
        val m = metrics.get(name)
        m.duration = time - m.startTime
        log(name, m)
        m.started = false
    }

    private fun log(name: String, m: Metric) {
        debug("$name : $m")
    }

    fun flush() {
        for (name in names) {
            val m = metrics.get(name)
            log(name, m)
        }
        names.clear()
        metrics.clear()

        writer!!.println("total: " + total)
    }

    private val time: Long
        get() = System.currentTimeMillis()

    fun addListener(listener: PerfEndedListener) {
        listeners.add(listener)
    }

    class Metric {
        var index: Int = 0
        var started: Boolean = false
        var startTime: Long = 0
        var duration: Long = 0

        override fun toString(): String {
            return "[started=$started, startTime=$startTime, duration=$duration]"
        }

    }

    interface PerfEndedListener {
        fun perfEnded(metric: Metric?, name: String?): Boolean
    }
}

fun profile (name: String, block: () -> Unit) {
    val index = Perf.start(name)
    block()
    Perf.end(index)
}