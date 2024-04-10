package org.yunghegel.salient.editor.app.dto.datatypes

import kotlinx.serialization.Serializable
import net.mgsx.gltf.scene3d.attributes.FogAttribute
import org.yunghegel.salient.editor.app.dto.DTOAdapter

@Serializable
data class FogData(
    val r: Float = 0.5f,
    val g: Float = 0.5f,
    val b: Float = 0.5f,
    val a: Float = 1f,
    val exponent: Float = 1f,
    val near: Float = 0.1f,
    val far: Float = 100f
) : DTOAdapter<FogAttribute, FogData> {

    override fun fromDTO(dto: FogData): FogAttribute {
        return FogAttribute.createFog(near,far,exponent)
    }

    companion object {
        val default = FogData(0.5f, 0.5f, 0.5f, 1f, 1f, 0.1f, 100f)
    }

    override fun toDTO(model: FogAttribute): FogData {
        return FogData(near = model.value.x, far = model.value.y, exponent = model.value.z)
    }
}