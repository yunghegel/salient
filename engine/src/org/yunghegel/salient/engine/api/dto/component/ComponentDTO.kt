package org.yunghegel.salient.engine.api.dto.component

import kotlinx.serialization.Serializable

@Serializable
sealed class ComponentDTO(var type : String) {

}
