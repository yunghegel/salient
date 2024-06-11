package org.yunghegel.salient.engine.system.file

import com.badlogic.gdx.files.FileHandle
import kotlinx.serialization.Serializable
import java.io.Writer
import java.nio.file.Path


@Serializable
class Filepath(val path:String) : Path by Path.of(path) {

    val handle : FileHandle
        get() = FileHandle(path)

    val writer : Writer
        get() = handle.writer(true)

    val exists: Boolean
        get()= handle.exists()

    val parent: Filepath
        get() = Filepath(path.substringBeforeLast("/"))

    val child : (String)-> Filepath
        get() = { name: String -> Filepath("$path/$name") }

    val list : List<FileHandle>
        get() = handle.list().toList()

    val children : Map<Filepath,FileHandle>
        get() =  handle.list().associate { file -> Filepath(file.path()) to file }

    val readString : String
        get() = handle.readString()

    val name: String
        get() = handle.nameWithoutExtension()

    val extension: String
        get() = handle.extension()

    val filename: String
        get() = name.substringBeforeLast(".")

    val lastModified : String
        get() = handle.lastModified().toString()

    val size : Long
        get() = handle.length()


    fun containsChild(file : FileHandle) : Boolean {
        return (!list.contains(file))
    }

    fun move(target: Filepath) = handle.moveTo(target.handle)

    fun copy(target: Filepath) = handle.copyTo(target.handle)

    fun delete() = handle.delete()

    fun mkfile() = handle.file().createNewFile()

    fun mkdir()= handle.file().mkdirs()

    fun isDescendentOf(other: Filepath) = path.startsWith(other.path)

    fun isParentOf(other: Filepath) = other.path.startsWith(path)

    override fun toString(): String {
        return path
    }


    companion object {
        fun String.pathOf() = Filepath(this)

        fun FileHandle.pathOf() = Filepath(path())

        fun isValidFilename(name: String) = name.matches(Regex("^[a-zA-Z0-9_\\-\\.]+$"))

        fun isValidPath(path: String) = path.matches(Regex("^[a-zA-Z0-9_\\-\\.\\/]+$"))

        fun isValidExtension(extension: String) = extension.matches(Regex("^[a-zA-Z0-9_\\-\\.]+$"))

        fun isValidName(name: String) = name.matches(Regex("^[a-zA-Z0-9_\\-]+$"))

        fun isValidDirname(name: String) = name.matches(Regex("^[a-zA-Z0-9_\\-]+$"))

        fun isValidDirpath(path: String) = path.matches(Regex("^[a-zA-Z0-9_\\-\\/]+$"))

        fun isValidFilepath(path: String) = path.matches(Regex("^[a-zA-Z0-9_\\-\\.\\/]+$"))

    }


}