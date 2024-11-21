package org.yunghegel.gdx.utils.ext

inline operator fun <reified T:Enum<T>> T.inc(): T {
    val values = enumValues<T>()
    val nextOrdinal = (ordinal + 1) % values.size
    return values[nextOrdinal]
}