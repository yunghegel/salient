package org.yunghegel.salient.engine.helpers

import com.badlogic.gdx.Gdx
import com.charleskorn.kaml.MultiLineStringStyle
import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.json.Json
import org.yunghegel.gdx.utils.data.serializerFor
import org.yunghegel.salient.engine.system.warn

object Serializer {

    val yaml : Yaml = Yaml(configuration = YamlConfiguration(
        multiLineStringStyle = MultiLineStringStyle.Plain,
        sequenceStyle = SequenceStyle.Block,
    ))

    val json : Json = Json {
        prettyPrint = true
        allowStructuredMapKeys = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    val gdxJson : GdxJson = GdxJson()

    inline fun <reified T:Any> fromYaml(yamlString: String, clazz: Class<T>): T {
        return try { T::class.fromYaml(yamlString) } catch (e: Exception) { throw e }
    }

    inline fun <reified T:Any> fromPath(path: String): T {
        return try { T::class.from(path) } catch (e: Exception) { throw e }
    }

    fun saveYaml(path: String, data: String) {
        save(path) { data }
    }

}