package org.yunghegel.gdx.cli

object StdOut {
    var writeLn : (String) -> Unit = { println(it) }
}