package org.yunghegel.gdx.utils.ext

fun stringToUnsignedCharByteBuffer(string: String): java.nio.ByteBuffer {
    val bytes = string.toByteArray()
    val buffer = java.nio.ByteBuffer.allocateDirect(bytes.size)
    buffer.put(bytes)
    buffer.flip()
    return buffer
}