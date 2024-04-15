package org.yunghegel.gdx.utils.ext

fun validateFloat(string: String): Boolean {
    return string.matches(Regex("[-+]?[0-9]*\\.?[0-9]+"))
}