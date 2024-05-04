package org.yunghegel.gdx.meshgen.math

import org.yunghegel.gdx.meshgen.data.attribute.ref.prop.StringAlias
import org.yunghegel.gdx.meshgen.data.attribute.ref.prop.TypeReference

class SetBitmask<Type>(val set: Set<Type>) where Type: StringAlias, Type: TypeReference {

    var mask = 0

//    create an indexed map of the set using its alias as key
    val uniqueValues = set.map { it.alias to set.indexOf(it) }.toMap()

    fun get(type: Type) : Boolean {
        return mask and (1 shl uniqueValues[type.alias]!!) != 0
    }

    fun set(type: Type, value: Boolean) {
        if (value) {
            mask = mask or (1 shl uniqueValues[type.alias]!!)
        } else {
            mask = mask and (1 shl uniqueValues[type.alias]!!).inv()
        }
    }

    fun set(vararg types: Type, value: Boolean) {
        for (type in types) {
            set(type,value)
        }
    }

    fun any(vararg types: Type): Boolean {
        for (type in types) {
            if (get(type)) {
                return true
            }
        }
        return false
    }

    fun all(vararg types: Type): Boolean {
        for (type in types) {
            if (!get(type)) {
                return false
            }
        }
        return true
    }

    fun none(vararg types: Type): Boolean {
        for (type in types) {
            if (get(type)) {
                return false
            }
        }
        return true
    }

    fun toggle(type: Type) {
        set(type,!get(type))
    }

    fun forEachTrue(action: (type: Type) -> Unit) {
        for (type in set) {
            if (get(type)) {
                action(type)
            }
        }
    }

    fun forEach(action: (type: Type, value: Boolean) -> Unit) {
        for (type in set) {
            action(type, get(type))
        }
    }

    fun forEachFalse(action: (type: Type) -> Unit) {
        for (type in set) {
            if (!get(type)) {
                action(type)
            }
        }
    }

    override fun toString() : String {
        val sb = StringBuilder()
        for (type in set) {
            sb.append(type.alias)
            sb.append(": ")
            sb.append(get(type))
            sb.append("\n")
        }
        return sb.toString()
    }








}