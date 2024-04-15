package org.yunghegel.salient.engine.system.storage

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

@Serializable(with = IndexSerializer::class)
@SerialName("Index")
class Index( val name:  String,  val type:String) {

    var id:Int = nextId()

    companion object {
        var _id = 0
        fun nextId() = _id++
    }



//    val getClass : Class<*>
//        get() = Class.forName(type)
//
//    fun validate(other:Class<*>): Boolean {
//        return getClass.isAssignableFrom(other)
//    }
}

object IndexSerializer : KSerializer<Index> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Index") {
        element<String>("id")
        element<String>("name")
        element<String>("type")
    }

    override fun deserialize(decoder: Decoder) : Index {
        return decoder.decodeStructure(descriptor) {
            var id = 0
            var name = ""
            var type = ""
            while (true) {
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> id = decodeIntElement(descriptor, index)
                    1 -> name = decodeStringElement(descriptor, index)
                    2 -> type = decodeStringElement(descriptor, index)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> error("Unexpected index: $index")
                }
            }
            Index(name,type)
        }
    }

    override fun serialize(encoder: Encoder, value: Index) {
        encoder.encodeStructure(descriptor) {
            encodeIntElement(descriptor, 0, value.id)
            encodeStringElement(descriptor, 1, value.name)
            encodeStringElement(descriptor, 2, value.type)
        }

    }
}