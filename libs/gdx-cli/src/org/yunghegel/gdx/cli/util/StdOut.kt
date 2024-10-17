package org.yunghegel.gdx.cli.util

object StdOut {
    var writeLn : (String) -> Unit = { println(it) }
}