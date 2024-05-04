package org.yunghegel.salient.engine.api.dto.component

import kotlinx.serialization.Serializable
import org.yunghegel.salient.engine.scene3d.component.ModelComponent

@Serializable
class ModelComponentDTO : ComponentDTO("ModelComponent") {

    var id: Int = 0
    var uuid: String = ""
    var strategy : ModelComponent.RetrievalStrategy = ModelComponent.RetrievalStrategy.FILE
    var retrieval : String = ""

}