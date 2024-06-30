package org.yunghegel.gdx.utils.data

import mobx.core.action
import mobx.core.observable
import squidpony.squidmath.EnumOrderedSet
import java.util.EnumSet

fun interface BitmaskPredicate<T : Enum<T>> {

    fun predicate(enum: Enum<T>): Boolean
}

fun interface BitmaskAction<T : Enum<T>> {

    fun action(enum: T)
}

fun interface PredicatedBitmaskAction<T : Enum<T>> {

    fun action(enum: T, value: Boolean)
}

class EnumBitmask<T : Enum<T>>(val enumClass: Class<T>) : BitmaskPredicate<T> , Mask {

    fun getTrue(): EnumOrderedSet<T> {
        val set = EnumOrderedSet<T>(enumClass)
        for (enum in enumClass.enumConstants) {
            if (get(enum)) set.add(enum)
        }
        return set
    }

    fun getFalse(): EnumOrderedSet<T> {
        val set = EnumOrderedSet<T>(enumClass)
        for (enum in enumClass.enumConstants) {
            if (!get(enum)) set.add(enum)
        }
        return set
    }

    fun eachTrue(action: (T) -> Unit) {
        for (enum in enumClass.enumConstants) {
            if (get(enum)) action(enum)
        }
    }

    override var mask by observable(0)

    fun set(enum: T, value: Boolean) {
        action {
            if (value) {
                mask = mask or (1 shl enum.ordinal)
            } else {
                mask = mask and (1 shl enum.ordinal).inv()
            }
        }

    }

    fun set(vararg enums: T, value: Boolean = true) {
        for (enum in enums) {
            set(enum, value)
        }
    }

    fun get(enum: Enum<T>): Boolean {
        return mask and (1 shl enum.ordinal) != 0
    }

    fun all() {
        for (enum in enumClass.enumConstants) {
            set(enum, true)
        }
    }

    fun none() {
        for (enum in enumClass.enumConstants) {
            set(enum, false)
        }
    }

    fun toggle(enum: T) {
        set(enum, !get(enum))
    }

    fun mask(): Int {
        return mask
    }

    fun fromMask(mask: Int) {
        for (enum in enumClass.enumConstants) {
            set(enum, mask and (1 shl enum.ordinal) != 0)
        }
    }

    fun copyFromMask(mask: Int): EnumBitmask<T> {
        val new = mask
        val copy = EnumBitmask(enumClass)
        copy.fromMask(new)
        return copy
    }

    fun copy(): EnumBitmask<T> {
        val copy = EnumBitmask(enumClass)
        copy.fromMask(mask)
        return copy
    }



    fun forEachTrue(action: BitmaskAction<T>) {
        for (enum in enumClass.enumConstants) {
            if (get(enum)) action.action(enum)
        }
    }

    fun forEachFalse(action: BitmaskAction<T>) {
        for (enum in enumClass.enumConstants) {
            if (!get(enum)) action.action(enum)
        }
    }

    override fun toString(): String {
        return enumClass.enumConstants.joinToString {
            "${it.name}=${get(it)}"
        }
    }

    override fun predicate(enum: Enum<T>): Boolean {
        return get(enum)
    }

    companion object {

        fun <T : Enum<T>> getFromMask(enumClass: Class<T>, mask: Int): EnumBitmask<T> {
            val bitmask = EnumBitmask(enumClass)
            bitmask.fromMask(mask)
            return bitmask
        }

        fun <T : Enum<T>> getValue(enum: T, bitmask: Int): Boolean {
            return bitmask and (1 shl enum.ordinal) != 0
        }

    }

}
