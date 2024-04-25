package org.yunghegel.salient.engine.api

import java.util.zip.CRC32

class Checksum() {

    val sum : CRC32 = CRC32()

    fun update(data: ByteArray) {
        sum.update(data)
    }

    fun from(string: String) {
        sum.update(string.toByteArray())
    }

    fun compare(checksum: Checksum): Boolean {
        return this.sum.value == checksum.sum.value
    }



}