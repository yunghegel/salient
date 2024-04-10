package org.yunghegel.salient.editor.app.storage

import com.charleskorn.kaml.SequenceStyle
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.json.Json

object Serializer {

    val yaml : Yaml = Yaml(configuration = YamlConfiguration(sequenceStyle = SequenceStyle.Flow ))
    val json : Json = Json { prettyPrint = true }



}