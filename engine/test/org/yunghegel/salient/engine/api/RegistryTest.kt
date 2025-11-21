package org.yunghegel.salient.engine.api

import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.yunghegel.salient.engine.api.model.Handle
import org.yunghegel.salient.engine.api.model.HandleType
import org.yunghegel.salient.engine.api.properties.NamedObjectResource
import org.yunghegel.salient.engine.helpers.KSClass
import org.yunghegel.salient.engine.system.file.Filepath
import kotlin.reflect.KClass

class RegistryTest {


    private val keyClass: KSClass<Handle> = Handle::class
    private val valueClass: KSClass<Handle> = Handle::class

    val key = Handle("path/to/key1.project")
    val value = Handle("path/to/value1.scene")

    @Test
    fun `register should add an entry to the registry`() {
        val registry = Registry(keyClass, valueClass)


        registry.register(key, value)

        assertTrue(registry.contains(key))
        assertEquals(value, registry.get(key))
    }

    @Test
    fun `unregister should remove an entry from the registry`() {
        val registry = Registry(keyClass, valueClass)


        registry.register(key, value)
        registry.unregister(key)

        assertFalse(registry.contains(key))
        assertNull(registry.get(key))
    }

    @Test
    fun `get should return the value associated with a key`() {
        val registry = Registry(keyClass, valueClass)


        registry.register(key, value)

        assertEquals(value, registry.get(key))
    }

    @Test
    fun `getOrThrow should return the value if the key exists`() {
        val registry = Registry(keyClass, valueClass)


        registry.register(key, value)

        assertEquals(value, registry.getOrThrow(key))
    }

    @Test
    fun `getOrThrow should throw NoSuchElementException if the key does not exist`() {
        val registry = Registry(keyClass, valueClass)

        val exception = assertThrows(NoSuchElementException::class.java) {
            registry.getOrThrow(key)
        }

    }

    @Test
    fun `clear should remove all entries from the registry`() {
        val registry = Registry(keyClass, valueClass)
        val key1 = Handle("path/to/key1.project")
        val value1 = Handle("path/to/value1.scene")
        val key2 = Handle("path/to/key2.project")
        val value2 = Handle("path/to/value2.scene")

        registry.register(key1, value1)
        registry.register(key2, value2)
        registry.clear()

        assertEquals(0, registry.size())
        assertFalse(registry.contains(key1))
        assertFalse(registry.contains(key2))
    }

    @Test
    fun `size should return the correct number of entries in the registry`() {
        val registry = Registry(keyClass, valueClass)


        registry.register(key, value)

        assertEquals(1, registry.size())
    }

    @Test
    fun `serialize and deserialize should work correctly`() {
        val keyClass: KClass<Handle> = Handle::class
        val valueClass: KClass<Handle> = Handle::class
        val registry = Registry(keyClass, valueClass)


        registry.register(key, value)
        val serialized = Registry.serialize(registry, keyClass, valueClass)
        println(serialized)
        val deserialized =
            Registry.deserialize(serialized, keyClass, valueClass)

        assertTrue(deserialized.contains(key))
        assertEquals(value, deserialized.get(key))
    }

    @Test
    fun `keys should return all registered keys`() {
        val registry = Registry(keyClass, valueClass)


        registry.register(key, value)

        assertEquals(setOf(key), registry.keys())
    }

    @Test
    fun `values should return all registered values`() {
        val registry = Registry(keyClass, valueClass)


        registry.register(key, value)

        assertEquals(listOf(value), registry.values().toList())
    }
}