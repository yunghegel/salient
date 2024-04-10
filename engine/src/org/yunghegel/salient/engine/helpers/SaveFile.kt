package org.yunghegel.salient.engine.helpers

import org.yunghegel.salient.engine.io.warn
import java.io.File
import java.io.FileWriter

class SaveFile(absolutePath: String) {

    private val adjustedPath = absolutePath

    init {
        val file = File(adjustedPath)
        if (!file.exists()) {
            file.createNewFile()
        }
    }

    fun saveToFile(content: String) {
        try {
            val fileWriter = FileWriter(adjustedPath, false)
            fileWriter.write(content)
            fileWriter.close()
        } catch (exception: Exception) {
            warn(exception.message!!)
        }
    }

    fun doesFileExist(): Boolean {
        val file = File(adjustedPath)
        return file.exists()
    }
}

fun save(absolutePath: String, content: ()->String) = SaveFile(absolutePath).saveToFile(content())
