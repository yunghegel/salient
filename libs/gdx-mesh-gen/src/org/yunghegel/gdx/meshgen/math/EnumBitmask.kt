package org.yunghegel.gdx.meshgen.math

import kotlin.reflect.KClass

class EnumBitmask<T : Enum<T>>(val enumClass: Class<T>) {

    constructor(enumClass: KClass<T>) : this(enumClass.java)

    private var bitmask: Int = 0

    fun set(enum: T, value: Boolean) {
        if (value) {
            bitmask = bitmask or (1 shl enum.ordinal)
        } else {
            bitmask = bitmask and (1 shl enum.ordinal).inv()
        }
    }

    fun get(enum: T): Boolean {
        return bitmask and (1 shl enum.ordinal) != 0
    }

    fun set(vararg enums: T, value: Boolean) {
        for (enum in enums) {
            set(enum,value)
        }
    }

    fun any(vararg enums: T): Boolean {
        for (enum in enums) {
            if (get(enum)) {
                return true
            }
        }
        return false
    }

    fun all(vararg enums: T): Boolean {
        for (enum in enums) {
            if (!get(enum)) {
                return false
            }
        }
        return true
    }

    fun none(vararg enums: T): Boolean {
        for (enum in enums) {
            if (get(enum)) {
                return false
            }
        }
        return true
    }

    fun toggle(enum: T) {
        set(enum,!get(enum))
    }

    fun toggle(vararg enums: T) {
        for (enum in enums) {
            toggle(enum)
        }
    }

    fun clear() {
        bitmask = 0
    }

    fun setAll(value: Boolean) {
        for (enum in enumClass.enumConstants) {
            set(enum,value)
        }
    }

    inline fun setEach(fn: (T)->Boolean) {
        for (enum in enumClass.enumConstants) {
            set(enum,fn(enum))
        }
    }

    inline fun withEach(fn: (T)->Unit) {
        for (enum in enumClass.enumConstants) {
            fn(enum)
        }
    }

    inline fun forEachTrue(fn: (T)->Unit) {
        for (enum in enumClass.enumConstants) {
            if (get(enum)) {
                fn(enum)
            }
        }
    }

    inline fun forEachFalse(fn: (T)->Unit) {
        for (enum in enumClass.enumConstants) {
            if (!get(enum)) {
                fn(enum)
            }
        }
    }

    override fun toString(): String {
        return enumClass.enumConstants.joinToString {
            "${it.name}=${get(it)}"
        }
    }

    infix fun has (other: T): EnumBitmask<T> {
        set(other,true)
        return this
    }

    infix fun not (other: T): EnumBitmask<T> {
        set(other,false)
        return this
    }

    infix fun query (other: T): Boolean {
        return get(other)
    }

    infix fun clear (other: T): EnumBitmask<T> {
        set(other,false)
        return this
    }

    infix fun switch (other: T): EnumBitmask<T> {
        if(this query other){
            this not other
        } else {
            this has other
        }
        return this
    }


}