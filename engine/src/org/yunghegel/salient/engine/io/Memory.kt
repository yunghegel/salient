package org.yunghegel.salient.engine.io

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.BufferUtils
import java.nio.IntBuffer

object Memory {

    private val intBuffer: IntBuffer = BufferUtils.newIntBuffer(16)

    const val GL_GPU_MEM_INFO_TOTAL_AVAILABLE_MEM_NVX: Int = 0x9048
    const val GL_GPU_MEM_INFO_CURRENT_AVAILABLE_MEM_NVX: Int = 0x9049

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

}