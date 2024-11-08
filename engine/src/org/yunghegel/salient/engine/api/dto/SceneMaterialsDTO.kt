package org.yunghegel.salient.engine.api.dto

import kotlinx.serialization.Serializable
import org.yunghegel.salient.engine.api.asset.type.spec.MaterialSpec

@Serializable
class SceneMaterialsDTO {

    var materials: MutableList<MaterialSpec> = mutableListOf()

}