package org.yunghegel.salient.engine.api.dto.component

import kotlinx.serialization.Serializable

@Serializable
class MaterialComponentDTO : ComponentDTO("MaterialComponent") {
    var usages = mutableListOf<String>()
}

open class AttributeDTO {
    open val alias : String = ""
    open val type : Long = 0L
}