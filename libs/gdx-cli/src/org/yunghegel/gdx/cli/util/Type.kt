package org.yunghegel.gdx.cli.util

enum class Type : TypeParser {
    STRING {
        override fun parse(value:String) : Any {
            return value
        }
    },

    INT {
        override fun parse(value: String): Any {
            return Integer.parseInt(value)
        }
    },
    FLOAT {
        override fun parse(value: String): Any {
            return value.toFloat()
        }
    },
    BOOLEAN {
        override fun parse(value: String): Any {
            return value.toBoolean()
        }
    };

    fun matches(value : Any) : Boolean {

        return when(this) {
            STRING -> value is String
            INT -> value is Int
            FLOAT -> value is Float
            BOOLEAN -> value is Boolean
        }
    }

    override fun toString(): String {
//        lowercase -> capitalize first letter
        return name.lowercase().capitalize()
    }

    companion object {
        fun fromClass(clazz: Class<*>): Type? {
            return when(clazz) {
                String::class.java -> STRING
                Int::class.java -> INT
                Float::class.java -> FLOAT
                Boolean::class.java -> BOOLEAN
                else -> null
            }
        }
    }





}
sealed interface TypeParser {
    fun parse(value : String) : Any
}