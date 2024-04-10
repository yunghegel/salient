package org.yunghegel.gdx.utils.data

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.serializer
import ktx.reflect.Reflection
import org.yunghegel.gdx.utils.data.ObjectWriter.Companion.yaml
import java.io.File
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class ObjectWriter<T:Any>(val value: T) {

    @OptIn(
        InternalSerializationApi::class, ExperimentalContracts::class, ExperimentalSerializationApi::class,
        Reflection::class
    )
    fun serialize(): String {
        return yaml.encodeToString(value::class.serializer() as SerializationStrategy<T>, value)
    }

    fun writeToFile(path: String,flush: Boolean = true) {
        try {

            val file =
                File(path)
            val writer = file.bufferedWriter()
            writer.use { out -> out.write(serialize()) }
            if(flush) {
                writer.flush()
                writer.close()
            }
        } catch (e: Exception) {
            System.out.print(e.stackTraceToString())
        }
    }

    companion object {
        val yaml: Yaml = Yaml.default
    }

}

@OptIn(ExperimentalContracts::class)
inline fun <reified T : Any> encode(value:  T): String {
    contract { returnsNotNull() implies (value is Serializable)  }
    return yaml.encodeToString(value.serializerFor(),  value)
}

@OptIn(InternalSerializationApi::class, ExperimentalContracts::class)
inline fun <reified T : Any> T.serializerFor(): SerializationStrategy<T> {
    return T::class.serializer()
}

@OptIn(InternalSerializationApi::class, ExperimentalContracts::class)
inline fun <reified T:Any> T.writeToFile(path: String, flush: Boolean = true) {
    val writer = ObjectWriter(this)
    writer.writeToFile(path, flush)
}