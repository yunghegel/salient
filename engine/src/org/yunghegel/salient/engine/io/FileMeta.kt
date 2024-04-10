package org.yunghegel.salient.engine.io

import com.badlogic.gdx.files.FileHandle

class FileMeta(file: FileHandle) {

    val path: String = file.path()

    val size : Long = file.length()

    val lastModified : Long = file.lastModified()

}