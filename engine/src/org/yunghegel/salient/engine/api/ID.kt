package org.yunghegel.salient.engine.api

import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.ObjectSet
import org.yunghegel.salient.engine.api.ID.Companion.generateUUID
import java.util.*

interface ID {

    val uuid: String

    val id: Long

    fun ID.generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    fun ID.uuidFrom(str: String): String {
        return UUID.fromString(str).toString()
    }

    fun ID.idFrom(str:String): Int {
        return str.hashCode()
    }

    fun ID.generateID(): Long {
        return _id++
    }

    companion object {

        val classCache = ObjectMap<Class<*>,Int>()

        private var _id = 0L

        fun ID.generateId(): Long {
            if (classCache.containsKey(this::class.java)) {
               val id = classCache[this::class.java] + 1
                classCache.put(this::class.java,id)
                return id.toLong()
            } else {
                classCache.put(this::class.java,0)
                return 0
            }
        }

        fun generateUUID(): String {
            return UUID.randomUUID().toString()
        }
    }

}