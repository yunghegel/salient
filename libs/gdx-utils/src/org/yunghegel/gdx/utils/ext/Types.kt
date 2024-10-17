package org.yunghegel.gdx.utils.ext

fun isPrimitive(type: Any): Boolean {
    return type is String || type is Int || type is Float || type is Boolean || type is Double || type is Long || type is Char || type is Short || type is Byte
}