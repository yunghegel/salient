package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.files.FileHandle
import kotlin.io.path.Path


fun Iterable<FileHandle>.withExtension(ext: String): List<FileHandle> {
    return this.filter { it.extension() == ext }
}

val FileHandle.name: String get() = nameWithoutExtension()

fun <T> FileHandle.getChild(path: String, action: (FileHandle)->T) : T? {
    val file = child(path)
    if (file.exists()) {
        return action(file)
    }
    return null
}

fun FileHandle.ls(action: (FileHandle)->Unit) {
    list().forEach { action(it) }
}

fun FileHandle.touch(path:String, action: (FileHandle)->Unit = {}) : FileHandle {
    val file = child(path)
    action(file)
    return file
}

fun FileHandle.dir(path:String, action: (FileHandle)->Unit = {}) : FileHandle {
    val file = child(path)
    if (!file.isDirectory) return file
    if (!file.exists()) file.mkdirs()
    action(file)
    return file
}

fun String.withExtension(ext: String): String {
    return "$this.$ext"
}

fun String.resolve(path: String): String {
    val actual = Path(this).resolve(path)
    return actual.toString()
}

fun String.relativeTo(path: String): String {
    val actual = Path(this).relativize(Path(path))
    return actual.toString()
}