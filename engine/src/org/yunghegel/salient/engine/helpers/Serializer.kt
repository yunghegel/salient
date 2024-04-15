package org.yunghegel.salient.engine.helpers

import com.charleskorn.kaml.MultiLineStringStyle
import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.json.Json

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



}