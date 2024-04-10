package org.yunghegel.salient.common.util

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap

class TypeGroup<T> : ObjectMap<Class<*>, Array<T>>() {

    fun getFor(type: Class<*>?): Array<T> {
        var list = get(type)
        if (list == null) {
            list = Array()
            put(type, list)
        }
        return list
    }

    fun addFor(type: Class<*>?, value: T) {
        getFor(type).add(value)
    }

    fun removeFor(type: Class<*>?, value: T, identity: Boolean) {
        getFor(type).removeValue(value, identity)
    }

}
