package org.yunghegel.salient.engine.system

import org.yunghegel.gdx.utils.reflection.Accessor
import org.yunghegel.gdx.utils.reflection.FieldAccessor
import org.yunghegel.salient.engine.system.storage.Registry
import kotlin.reflect.KProperty

val props = SystemRegistry.properties_index

val types = SystemRegistry.type_registry

val store = SystemRegistry.object_store

object SystemRegistry {

    val type_registry = Registry<Class<*>>("type")

    val properties_index : MutableMap<String,FieldAccessor> = mutableMapOf()

    val object_store = Registry<Any>("store")

}

fun set_property(name: String,accessor: FieldAccessor): Accessor {
    SystemRegistry.properties_index[name] = accessor
    return accessor
}

fun get_property(name: String): Accessor? {
    return SystemRegistry.properties_index[name]
}

fun register_type(scope:String="default",type: Class<*>): Class<*> {
    SystemRegistry.type_registry[scope,type.name] = type
    return type
}

fun store_object(scope:String="default",name: String,instance: Any): Any {
    SystemRegistry.object_store[scope,name] = instance
    return instance
}

inline fun <reified T,V:Any> T.store(name: String,value: V) {
    store_object(T::class.simpleName!!,name,value)
}

fun get_object(scope:String="default",name: String): Any? {
    return SystemRegistry.object_store[scope,name]
}

infix fun <T:Any> T.export(prop: KProperty<*>): T {
    store_object(this::class.simpleName!!,prop.name,prop.getter.call()!!)
    return this
}

infix fun <T:Any> T.exports(run: T.()->Unit): T {
    this.run()
    return this
}

