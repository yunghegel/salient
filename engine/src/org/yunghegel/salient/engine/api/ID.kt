package org.yunghegel.salient.engine.api

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.ObjectMap
import java.util.*

interface ID {

    val uuid: String

    val id: Int

    fun ID.generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    fun ID.generateID(): Int {
        return _id++
    }

    companion object {

        val classCache = ObjectMap<Class<*>,Int>()

        private var _id = 0

        fun ID.generateId(): Int {
            if (classCache.containsKey(this::class.java)) {
               val id = classCache[this::class.java] + 1
                classCache.put(this::class.java,id)
                return id
            } else {
                classCache.put(this::class.java,0)
                return 0
            }
        }

        fun generateUUID(): String {
            return UUID.randomUUID().toString()
        }

        fun writeFile(id:ID,directory: String) {
            val file = FileHandle(directory).child("${id.uuid}.uuid")
            file.file().createNewFile()
            file.writeString(id.uuid,false)
        }

        fun make(id: Int,uuid:String): ID {
            return object : ID {
                override val uuid: String = uuid
                override val id: Int = id
            }
        }
    }

}