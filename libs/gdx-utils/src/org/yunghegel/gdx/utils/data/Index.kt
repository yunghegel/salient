package org.yunghegel.gdx.utils.data


class Index<T> where T:Named {

    val types = mutableMapOf<Class<out T>,MutableList<T>>()

    fun index(type:Class<out T>,value:T) {
        if (!types.containsKey(type)) {
            types[type] = mutableListOf()
        }
        types[type]?.add(value)
    }

    fun list(type:Class<out T>):MutableList<T>? {
        return types[type]
    }

    fun List<T>.named (name:String):T? {
        return find { it.name == name }
    }

    fun get(type:Class<out T>,name:String):T? {
        return types[type]?.find { it.name == name }
    }



}