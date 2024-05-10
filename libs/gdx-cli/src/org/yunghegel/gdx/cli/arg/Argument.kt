package org.yunghegel.gdx.cli.arg

data class Argument(val name: String, val value : Any, val kind: ArgumentKind) {

    init {
        println(this)
    }

    override fun toString(): String {
        return "$name: $value"
    }

}