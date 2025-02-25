package org.yunghegel.salient.engine.api.model

enum class HandleType(vararg val extension: String) {
    PROJECT("salient"),
    SCENE("scene"),
    ASSET("mesh", "texture", "material", "shader", "model"),
    INDEX("index"),
    REGISTRY("registry"),
    OTHER("*");

    private var idCounter = 0

    fun nextId(): Int {
        return (ordinal shl 16) + idCounter++
    }


    companion object {


        fun resolveType(ext: String): HandleType {
            entries.forEach { type ->
                if (type.extension.contains(ext)) {
                    return type
                }
            }
            return OTHER
        }
    }
}