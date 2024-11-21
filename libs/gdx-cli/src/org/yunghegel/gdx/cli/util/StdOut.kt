package org.yunghegel.gdx.cli.util

object StdOut {
    var writeLn : MutableList<(String) -> Unit> = mutableListOf({ println(it) })

    var writeErr : MutableList<(String) -> Unit> = mutableListOf({ System.err.println(it) })

    fun writeLn(message: String) {
        writeLn.forEach { it(message) }
    }

    fun writeErr(message: String) {
        writeErr.forEach { it(message) }
    }

}