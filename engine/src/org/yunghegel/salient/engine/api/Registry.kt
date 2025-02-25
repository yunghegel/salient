package org.yunghegel.salient.engine.api

import elemental2.core.JsObject.entries
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.Json
import org.yunghegel.gdx.utils.KClassSerializer
import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.salient.engine.api.properties.NamedObjectResource
import org.yunghegel.salient.engine.helpers.IntSerializer
import org.yunghegel.salient.engine.helpers.KSClass
import org.yunghegel.salient.engine.helpers.KSClassSerializer
import org.yunghegel.salient.engine.helpers.Serializer
import kotlin.collections.toSet
import kotlin.reflect.KClass


@Serializable
class Registry<K, V>(
    private val keyClass: KSClass<K>,
    private val valueClass: KSClass<V>,
    private val entries: MutableMap<Int, V> = mutableMapOf(),
    private val references: MutableList<K> = mutableListOf()
) where K : Any, K : NamedObjectResource, V : Any, V : NamedObjectResource {


    fun register(key: K, value: V) {
        val id = key.id
        entries[id] = value
        references.add(key)
    }

    fun unregister(key: K) {
        val id = key.id
        entries.remove(id)
        references.remove(key)
    }

    fun get(key: K): V? {
        val id = key.id
        return entries[id]
    }

    fun getOrThrow(key: K): V {
        return get(key) ?: throw NoSuchElementException("No entry found for key $key")
    }

    fun contains(key: K): Boolean {
        val id = key.id
        return entries.containsKey(id)
    }

    fun keys(): Set<K> {
        return references.toSet()
    }

    fun values(): Collection<V> {
        return entries.values
    }

    fun size(): Int {
        return entries.size
    }

    fun isEmpty(): Boolean {
        return entries.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return entries.isNotEmpty()
    }

    fun clear() {
        entries.clear()
        references.clear()
    }

    class Serializer<K, V>(val keyClass: KSClass<K>, val valueClass: KSClass<V>) :
        KSerializer<Registry<K, V>> where K : Any, K : NamedObjectResource, V : Any, V : NamedObjectResource {

        @OptIn(InternalSerializationApi::class)
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Registry") {
            element("entries", MapSerializer(IntSerializer, valueClass.serializer()).descriptor, isOptional = true)
            element("references", ListSerializer(keyClass.serializer()).descriptor, isOptional = true)
        }

        @OptIn(InternalSerializationApi::class)
        override fun serialize(encoder: Encoder, value: Registry<K, V>) {
            encoder.encodeStructure(descriptor) {
                encodeSerializableElement(
                    descriptor,
                    0,
                    MapSerializer(IntSerializer, valueClass.serializer()),
                    value.entries
                )
                encodeSerializableElement(descriptor, 1, ListSerializer(keyClass.serializer()), value.references)
            }
        }

        @OptIn(InternalSerializationApi::class)
        override fun deserialize(decoder: Decoder): Registry<K, V> {
            return decoder.decodeStructure(descriptor) {
                var entries: MutableMap<Int, V>? = null
                var references: MutableList<K>? = null
                while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        CompositeDecoder.DECODE_DONE -> break
                        0 -> entries = decodeSerializableElement(
                            descriptor,
                            0,
                            MapSerializer(IntSerializer, valueClass.serializer())
                        ).toMutableMap()

                        1 -> references = decodeSerializableElement(
                            descriptor,
                            1,
                            ListSerializer(keyClass.serializer())
                        ).toMutableList()

                        else -> error("Unexpected index: $index")
                    }
                }
                Registry(keyClass, valueClass, entries!!, references!!)
            }
        }

    }

    companion object {

        fun <K, V> serialize(
            registry: Registry<K, V>,
            keyClass: KSClass<K>,
            valueClass: KSClass<V>
        ): String where K : Any, K : NamedObjectResource, V : Any, V : NamedObjectResource {
            val serializer = Serializer(keyClass, valueClass)
            return org.yunghegel.salient.engine.helpers.Serializer.json.encodeToString(serializer, registry)
        }

        fun <K, V> deserialize(
            serialized: String,
            keyClass: KSClass<K>,
            valueClass: KSClass<V>
        ): Registry<K, V> where K : Any, K : NamedObjectResource, V : Any, V : NamedObjectResource {
            val serializer = Serializer(
                keyClass, valueClass
            )
            return org.yunghegel.salient.engine.helpers.Serializer.json.decodeFromString(serializer, serialized)
        }
    }


}