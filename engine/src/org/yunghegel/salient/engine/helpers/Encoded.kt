package org.yunghegel.salient.engine.helpers

import kotlinx.serialization.*
import java.io.File
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

@Serializable
class Encoded<T> (var data: String="") {

    @Transient
    var ref : T? = null

    @Transient
    var type : Class<T>? = null



    constructor(data: String, ref: T) : this(data) {
        this.ref = ref
    }

    constructor (clazz: Class<T>) : this() {
        this.type = clazz
    }



    infix fun to(path:String) {
        save(path) { data }
    }

    inline infix fun <reified T:Any> from(path: String) : T {
        return Serializer.yaml.decodeFromString<T>(File(path).readText())
    }

    val encodeable get() = data::class.annotations.filterIsInstance<Serializable>().isNotEmpty()

    companion object {
        inline fun <reified T:Any> T.encoded(obj:T) : Encoded<T> {
            return serialize()
        }

    }

}
inline fun <reified T:Any> T.serialize() : Encoded<T> {
    val serializable = T::class.annotations.filterIsInstance<Serializable>().isNotEmpty()
    return encode(this)
}

inline fun <reified T:Any> encode(value: T) : Encoded<T> {
    val serializable = T::class.annotations.filterIsInstance<Serializable>().isNotEmpty()
    return Encoded(encodestring(value),value)
}

inline infix fun <reified T:Any> KClass<T>.from(path: String) : T {
    return try { Serializer.yaml.decodeFromString<T>(File(path).readText()) } catch (e: Exception) { throw e }
}


@OptIn(ExperimentalContracts::class, InternalSerializationApi::class)
inline fun <reified T : Any> encodestring(value:  T): String {
    contract { returnsNotNull() implies (value is Serializable)  }
    val serializer = T::class.serializerOrNull()
    return if (serializer!=null) Serializer.yaml.encodeToString(serializer, value) else (value.toString())

}