package org.yunghegel.salient.editor.app.dto.datatypes

import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import kotlinx.serialization.Serializable
import org.yunghegel.salient.editor.app.dto.DTOAdapter

@Serializable
data class AmbientLightData(val color: ColorData = ColorData(), val intensity: Float = 1f) : DTOAdapter<ColorAttribute,AmbientLightData> {

    override fun fromDTO(dto: AmbientLightData): ColorAttribute {
        return ColorAttribute.createAmbient(dto.color.r,dto.color.g, dto.color.b, dto.intensity)
    }

    override fun toDTO(model: ColorAttribute): AmbientLightData {
        require (model.type == ColorAttribute.AmbientLight) { "Model is not an AmbientLight" }
        return AmbientLightData(ColorData.fromColor(model.color), model.color.a)
    }

}