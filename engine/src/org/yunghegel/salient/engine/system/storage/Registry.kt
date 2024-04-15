package org.yunghegel.salient.engine.system.storage

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import org.yunghegel.salient.common.util.TypeMap
import org.yunghegel.salient.engine.api.Named

@Serializable
open class Registry<T>(override val name: String = "null") : Named {

    var autosave:Boolean = true

    @Transient
    val keys : MutableMap<String, Entry<T>> = mutableMapOf()

    private val store  = mutableListOf<Entry<T>>()

    val global = scope("global")

    operator fun get(scope:String="default",name: String): T? {
        val key = keys.keys.firstOrNull { it == scope } ?: return null
        return keys[key]?.values?.firstOrNull { it.name == name }?.value
    }

    operator fun set(scope: String = "default",name:String, value : T) {
        index(scope,name,value)
    }

    infix fun scope(scope:String) : Entry<T>? {
        return keys[keys.keys.firstOrNull { it == scope }]
    }



    fun index(scope: String="global", name: String, value:T) {
        val owner  = keys.keys.firstOrNull { it == name }
        if (owner == null) keys[scope] = Entry(mutableListOf())

        val entry = keys.getOrPut(scope) { Entry() }
        entry.values.add(Value(name, value))
        if (!store.contains(entry)) store.add(entry)
    }

    @Transient
    val types = TypeMap<Class<out T>>()

     open fun resolvePath():String {
        return "registry.yaml"
     }



    init {

    }

     object RegistrySerializer: KSerializer<Map<Index,String>> by MapSerializer(Index.serializer(),String.serializer())  {
        inline fun <reified T:Any> Registry<Any>.index(name:String, value:T) {
        }

//        inline fun <reified R,reified T:Any> Registry<T>.find(name: String): T? {
//            return store[Index(R::class.java.name,name)]?.ref as T
//        }
    }

}



