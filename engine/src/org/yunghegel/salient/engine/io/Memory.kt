package org.yunghegel.salient.engine.io

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.BufferUtils
import java.lang.management.ManagementFactory
import java.lang.management.OperatingSystemMXBean
import java.nio.IntBuffer

object Memory {

    private val intBuffer: IntBuffer = BufferUtils.newIntBuffer(16)

    const val GL_GPU_MEM_INFO_TOTAL_AVAILABLE_MEM_NVX: Int = 0x9048
    const val GL_GPU_MEM_INFO_CURRENT_AVAILABLE_MEM_NVX: Int = 0x9049

    val operatingSystemMXBean =  ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean;

    data class MemoryReport(val os:Float)



    val gLMaxMemoryKB: Int
        get() {
            intBuffer.clear()
            Gdx.gl.glGetIntegerv(GL_GPU_MEM_INFO_TOTAL_AVAILABLE_MEM_NVX, intBuffer)
            return intBuffer.get()
        }

    val gLAvailableMemoryKB: Int
        get() {
            intBuffer.clear()
            Gdx.gl.glGetIntegerv(GL_GPU_MEM_INFO_CURRENT_AVAILABLE_MEM_NVX, intBuffer)
            return intBuffer.get()
        }

    val jvmAvailableMemoryKB: Int
        get() {
            val runtime = Runtime.getRuntime()
            return (runtime.freeMemory() / 1024).toInt()
        }

    val jvmMaxMemoryKB: Int
        get() {
            val runtime = Runtime.getRuntime()
            return (runtime.maxMemory() / 1024).toInt()
        }

    val glUsedMemoryKB: Int
        get() = gLMaxMemoryKB - gLAvailableMemoryKB

    val jvmUsedMemoryKB: Int
        get() = jvmMaxMemoryKB - jvmAvailableMemoryKB

    val getPercent: (Int, Int) -> Float = { used, total -> used.toFloat() / total.toFloat() * 100 }


    enum class Units {
        KB, MB, GB;

        fun convert(value: Int): Float {
            return when (this) {
                KB -> value.toFloat()
                MB -> value.toFloat() / 1024
                GB -> value.toFloat() / 1024 / 1024
            }
        }

    }

    enum class Type {
        JVM, GL;
    }

    fun getUsedMemory(unit: Units, type: Type): Float {
        return when (type) {
            Type.JVM -> unit.convert(jvmUsedMemoryKB)
            Type.GL -> unit.convert(glUsedMemoryKB)
        }
    }

    fun getAvailableMemory(unit: Units, type: Type): Float {
        return when (type) {
            Type.JVM -> unit.convert(jvmAvailableMemoryKB)
            Type.GL -> unit.convert(gLAvailableMemoryKB)
        }
    }

    fun getMaxMemory(unit: Units, type: Type): Float {
        return when (type) {
            Type.JVM -> unit.convert(jvmMaxMemoryKB)
            Type.GL -> unit.convert(gLMaxMemoryKB)
        }
    }

    fun getPercentage(units:Units=Units.MB,type: Type): Float {
        return getPercent(getUsedMemory(units,type).toInt(),getMaxMemory(units,type).toInt())
    }





}