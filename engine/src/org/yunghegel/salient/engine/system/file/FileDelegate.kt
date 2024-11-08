package org.yunghegel.salient.engine.system.file

import com.badlogic.gdx.files.FileHandle
import kotlin.properties.ReadWriteProperty

class FileDelegate(var file: FileHandle, var dir: Boolean = false) : ReadWriteProperty<Any, FileHandle> {

    init {
        if (!file.exists()) {
            if (dir) file.mkdirs()
            else file.file().createNewFile()
        }
    }

    override fun getValue(thisRef: Any, property: kotlin.reflect.KProperty<*>): FileHandle {
        return file
    }

    override fun setValue(thisRef: Any, property: kotlin.reflect.KProperty<*>, value: FileHandle) {
        file = value
    }

    operator fun setValue(thisRef: Any, property: kotlin.reflect.KProperty<*>, value: String) {
        file.writeString(value, false)
    }
}

fun file(file: FileHandle) = FileDelegate(file)

fun FileHandle.dirchild(path: String) : FileDelegate {
    return FileDelegate(this.child(path))
}