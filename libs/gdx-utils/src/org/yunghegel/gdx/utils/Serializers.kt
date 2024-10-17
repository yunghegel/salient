package org.yunghegel.gdx.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass


class KClassSerializer : KSerializer<KClass<*>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("kotlin.reflect.KClass", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: KClass<*>) {
        encoder.encodeString(value.qualifiedName!!)
    }

    override fun deserialize(decoder: Decoder): KClass<*> {
        val className = decoder.decodeString()
        return Class.forName(className).kotlin
    }
}
