package org.yunghegel.gdx.utils.data

interface EnumMask<T:Enum<T>> {



    val bitmask : EnumBitmask<T>

    val entries : List <T>
        get () = bitmask.enumClass.enumConstants.toList()

    infix fun set(value: T) {
        bitmask.set(value,true)
    }

    infix fun set(string: String) {
        entries.find { it.name == string }?.let { bitmask.set(it,true) }
    }

    infix fun clear(value: T) {
        bitmask.set(value,false)
    }

    infix fun clear(string: String) {
        entries.find { it.name == string }?.let { bitmask.set(it,false) }
    }


    infix fun toggle(value: T) {
        bitmask.set(value,!bitmask.get(value))
    }

    infix fun toggle(string: String) {
        entries.find { it.name == string }?.let { bitmask.set(it,!bitmask.get(it)) }
    }

    infix fun has(value: T): Boolean {
        return bitmask.get(value)
    }

    infix fun has(string: String): Boolean {
        return entries.find { it.name == string }?.let { bitmask.get(it) } ?: false
    }

    fun setAll(vararg values: T) {
        values.forEach { bitmask.set(it,true) }
    }

    fun clearAll(vararg values: T) {
        values.forEach { bitmask.set(it,false) }
    }

    fun initialize(from: Int) {
        bitmask.fromMask(from)
    }

    fun reportEach() {
        entries.filter { this has it }.run {
            println("HAS: [${joinToString(",")}]")
        }
        entries.filter {  !it }.run {
            println("HAS NOT: [${joinToString(",")}]")
        }
    }

    operator fun T.not(): Boolean {
        return !bitmask.get(this)
    }

    operator fun T.unaryPlus() {
        bitmask.set(this,true)
    }

    operator fun T.unaryMinus() {
        bitmask.set(this,false)
    }

    operator fun contains (value: T): Boolean {
        return bitmask.get(value)
    }


}