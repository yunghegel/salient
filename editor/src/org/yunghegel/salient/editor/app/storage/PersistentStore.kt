package org.yunghegel.salient.editor.app.storage

import com.badlogic.gdx.utils.Json
import com.charleskorn.kaml.Yaml
import kotlinx.serialization.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import org.yunghegel.salient.engine.events.lifecycle.onShutdown
import org.yunghegel.salient.engine.helpers.reflect.Type
import org.yunghegel.salient.engine.helpers.reflect.annotation.Key
import org.yunghegel.salient.engine.io.Paths
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
class Persistence<Any,T> (val key:String, var value: T): ReadWriteProperty<Any, T> {

    init {

        insertIfMissing()

    }

    fun insertIfMissing() {
        if (map[key] == null) {
            map[key] = value.toString()
        }
    }

    fun findAnnotation() : Key? {


        return null
    }

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        val value = map[key] ?: value.toString()
        return parse(value,findAnnotation()?.type ?: Type.String)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        map[key] = value.toString()
    }
    fun parse(value: String, type: Type): T {
        return when(type) {
            Type.Float -> value.toFloat() as T
            Type.Int -> value.toInt() as T
            Type.String -> value as T
            Type.Boolean -> value.toBoolean() as T
            else -> value as T
        }
    }

    companion object {

        val map  = HashMap<String,String>()

        @Serializable
        data class Store(val key_value_store: HashMap<String,String>)

        val persistent_storage = Store(map)


        @Transient
        val file = Paths.SALIENT_HOME.child("key_value_store.yaml")

        init {

            if (file.exists) {
                val yaml = Yaml.default
                val store = if (file.readString.isEmpty()) Store(HashMap()) else yaml.decodeFromString(Store.serializer(), file.readString)
                map.putAll(store.key_value_store)
            }

            onShutdown {
                save()
            }

        }

        fun save(json: Json = Json()) {

            val yaml = Yaml.default
            val str = yaml.encodeToString(persistent_storage)
            val file = Paths.SALIENT_HOME.child("key_value_store.yaml")
            file.delete()
            file.mkfile()
            val writer = file.writer
            writer.write(str)
            writer.flush()
            writer.close()
        }

    }
}

@OptIn(ExperimentalSerializationApi::class)


fun <T> persistent(key:String,value: T) : ReadWriteProperty<Any, T> {
    val persistence = Persistence<Any,T>(key,value) as ReadWriteProperty<Any, T>
    return persistence
}

fun <T:Any> KProperty<*>.persist_key(key:String) : ReadWriteProperty<Any, T> {
    val persistence = Persistence<Any,T>(key,this.getter!!.call()!! as T) as ReadWriteProperty<Any, T>
    return persistence
}