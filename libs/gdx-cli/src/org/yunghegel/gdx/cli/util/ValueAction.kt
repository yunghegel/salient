package org.yunghegel.gdx.cli.util

enum class ValueAction(val keywords: List<String>) {
    SET(listOf("put","set")), PRINT(listOf("print","get")), HELP(listOf("help","?","--h","-h"));

    companion object {
        fun fromString(str: String): ValueAction? {
            for (action in entries) {
                if (action.keywords.contains(str)) {
                    return action
                }
            }
            return null
        }

        val all = entries.map { it.keywords }.flatten()
    }
}