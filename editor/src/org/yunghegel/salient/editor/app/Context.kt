package org.yunghegel.salient.editor.app

interface Context {

    val storage : HashMap<String,Any>

    operator fun get(key: String) : Any? {
        return storage.get(key)
    }

    operator fun set(key: String, value: Any) {
        storage.set(key, value)
    }
}