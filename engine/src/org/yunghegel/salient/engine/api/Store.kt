package org.yunghegel.salient.engine.api

interface Store {

    val map : MutableMap<String,String >

    fun store(key: String, value: String) {
        map[key] = value
    }

    fun stored(key: String) : String? {
        return map[key]
    }

    fun clearStored(key: String) {
        map.remove(key)
    }

    infix fun contains(key: String) : Boolean {
        return map.containsKey(key)
    }

    infix fun store(pair: Pair<String,String>) {
        map[pair.first] = pair.second
    }

    infix fun store(keyValue:String) {
        if(keyValue. contains("=")) {
            val (key, value) = keyValue.split("=")
            map[key] = value
        } else if (keyValue.contains(":")) {
            val (key, value) = keyValue.split(":")
            map[key.trim()] = value.trim()
        } else if (keyValue.contains("->")) {
            val (key, value) = keyValue.split("->")
            map[key] = value
        } else if (keyValue.contains(" ")) {
            val (key, value) = keyValue.split(" ")
            map[key] = value
        }
    }

    infix fun retrieve(key: String) : String? {
        return map[key]
    }







}