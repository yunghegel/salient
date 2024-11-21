package org.yunghegel.salient.engine.api.asset

import com.badlogic.gdx.utils.ObjectMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import ktx.collections.set

import org.yunghegel.gdx.utils.data.serialize
import org.yunghegel.salient.engine.helpers.encodestring


@Serializable()
class Extras(@Transient private val input: Map<String,Any> = mapOf())  {


    val map = HashMap<String,String>()

    init {
        input.forEach { (key, value) ->
            map[key] = if (value is String) value else value.serialize(false)
        }
    }

    fun setString(key: String, value: String) {
        map.put(key, value)
    }

    operator fun set(key: String, value: Any) {
        if (value is String) {
            map.put(key, value)
            return
        }
        val _key = "${value::class.simpleName}.${key}"
        this[key] =  encodestring(value,false)
    }

}

//object ExtrasSerializer : KSerializer<Extras> {
//    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Extras") {
//        element("map", HashMapSerializer.descriptor)
//    }
//
//    override fun serialize(encoder: Encoder, value: Extras) {
//        encoder.encodeStructure(descriptor) {
//            encodeSerializableElement(descriptor, 0, MapSerializer(String.serializer(), String.serializer()), value.map)
//        }
//    }
//
//    override fun deserialize(decoder: Decoder): Extras {
//        return decoder.decodeStructure(descriptor) {
//            val map = decodeSerializableElement(descriptor, 0, MapSerializer(String.serializer(), String.serializer()))
//            Extras().apply {
//                this.map.putAll(map)
//            }
//        }
//    }
//}

//object HashMapSerializer : KSerializer<HashMap<String, String>> {
//    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("HashMap") {
//        element("map", MapSerializer(String.serializer(), String.serializer()).descriptor)
//    }
//
//    override fun serialize(encoder: Encoder, value: HashMap<String, String>) {
//        encoder.encodeStructure(descriptor) {
//            encodeSerializableElement(descriptor, 0, MapSerializer(String.serializer(), String.serializer()), value)
//        }
//    }
//
//    override fun deserialize(decoder: Decoder): HashMap<String, String> {
//        return decoder.decodeStructure(descriptor) {
//            val map = decodeSerializableElement(descriptor, 0, MapSerializer(String.serializer(), String.serializer()))
//            HashMap(map)
//        }
//    }
//}

fun props(vararg pairs: Pair<String,Any>) :Extras {
    val map = HashMap<String,Any>()
    pairs.forEach { (key, value) ->
        map[key] = value
    }
    return Extras(map)
}

