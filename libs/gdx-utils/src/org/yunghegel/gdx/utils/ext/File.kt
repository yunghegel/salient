package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.files.FileHandle


fun Iterable<FileHandle>.withExtension(ext: String): List<FileHandle> {
    return this.filter { it.extension() == ext }
}

