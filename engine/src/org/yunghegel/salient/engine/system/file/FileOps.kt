package org.yunghegel.salient.engine.system.file

import com.badlogic.gdx.files.FileHandle
import org.yunghegel.salient.engine.system.debug
import org.yunghegel.salient.engine.system.info
import java.io.File

object FileOps {

    fun copy(source: FileHandle, destination: FileHandle) {
        if (source.isDirectory) {
            copyDirectory(source, destination)
        } else {
            info("copying ${source.name()} to ${destination.path()}")
            source.copyTo(destination)
        }
    }

    fun copyDirectory(source: FileHandle, destination: FileHandle) {
        if (!source.isDirectory) {
            throw IllegalArgumentException("Source ($source) must be a directory.")
        }
        if (!destination.exists()) {
            destination.mkdirs()
        }
        if (!destination.isDirectory) {
            throw IllegalArgumentException("Destination ($destination) must be a directory.")
        }
        for (child in source.list()) {
            val childDestination = destination.child(child.name())
            if (child.isDirectory) {
                copyDirectory(child, childDestination)
            } else {
                child.copyTo(childDestination)
            }
        }
    }

    fun addChildToDir(directory: FileHandle,child:FileHandle) {
        val targetpath = directory.child(child.name())
        debug("Adding child to directory; target $targetpath")
    }

    fun move(source: FileHandle, destination: FileHandle) {
        source.moveTo(destination)
    }

    fun delete(file: FileHandle) {
        file.delete()
    }

    fun rename(file: FileHandle, newName: String) {
        file.file().renameTo(File(file.parent().path() + "/" + newName))
    }

}