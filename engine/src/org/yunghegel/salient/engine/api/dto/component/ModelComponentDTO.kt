package org.yunghegel.salient.engine.api.dto.component

import kotlinx.serialization.Serializable
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.scene3d.component.ModelComponent

@Serializable
class ModelComponentDTO : ComponentDTO("ModelComponent") {

    /**
     * The UUID of the asset handle of the model, to be used to locate the asset in the project/scene
     */
    var asset: String? = null

    /**
     * The strategy used to import the model; either FILE or PRIMITIVE
     */
    var strategy : ModelComponent.Type?  = null

    /**
     * The import parameters used to import the model
     */
    var importParams = mutableMapOf<String, String>()



}