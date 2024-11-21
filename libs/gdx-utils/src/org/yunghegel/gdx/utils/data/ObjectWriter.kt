package org.yunghegel.gdx.utils.data

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.*
import ktx.reflect.Reflection
import java.io.File
import kotlin.contracts.ExperimentalContracts

class ObjectWriter<T:Any>(val value: T) {

    @OptIn(
        InternalSerializationApi::class, ExperimentalContracts::class, ExperimentalSerializationApi::class,
        Reflection::class
    )
    fun serialize(json: Boolean = false): String {
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
        @OptIn(ExperimentalSerializationApi::class)
        val yaml: Yaml = Yaml.default
    }

}







@OptIn(InternalSerializationApi::class, ExperimentalContracts::class)
inline fun <reified T : Any> T.serializerFor(): SerializationStrategy<T> {
    return T::class.serializer()
}


inline fun <reified T : Any> T.serialize(json:Boolean = false): String {
    return ObjectWriter(this).serialize(json)
}

@OptIn(InternalSerializationApi::class, ExperimentalContracts::class)
inline fun <reified T:Any> T.writeToFile(path: String, flush: Boolean = true) {
    val writer = ObjectWriter(this)
    writer.writeToFile(path, flush)
}

inline fun <reified T: Any> T.validSerializer(): Boolean {
    return this::class.annotations.filterIsInstance<Serializable>().isNotEmpty()
}