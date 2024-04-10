package org.yunghegel.salient.engine.api

interface Tagged {

    val tags : MutableSet<String>

    fun tag(tag: String) {
        tags.add(tag)
    }

    fun removeTag(tag: String) {
        tags.remove(tag)
    }



    fun taggedAny(vararg tags: String) : Boolean {
        return tags.any { tagged(it) }
    }

    infix fun tagged(tag: String) : Boolean {
        return tags.contains(tag)
    }

}