package org.yunghegel.salient.editor.app.dto.datatypes

import kotlinx.serialization.Serializable
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx
import org.yunghegel.salient.editor.app.dto.DTOAdapter

@Serializable
data class DirectionalLightData(
    val x: Float=-.8f,
    val y: Float=-.2f,
    val z: Float=-.4f,
    val r: Float= 1f,
    val g: Float= 1f,
    val b: Float= 1f,
    val a: Float= 1f
) : DTOAdapter<DirectionalLightEx, DirectionalLightData> {

    companion object {
        val default = DirectionalLightData(-.8f, .2f, .4f, 1f, 1f, 1f, 20f)
    }

    override fun fromDTO(dto: DirectionalLightData): DirectionalLightEx {
        return DirectionalLightEx().apply {
            direction.set(dto.x, dto.y, dto.z)
            color.set(dto.r, dto.g, dto.b, dto.a)
        }
    }

    override fun toDTO(model: DirectionalLightEx): DirectionalLightData {
        return DirectionalLightData(
            model.direction.x,
            model.direction.y,
            model.direction.z,
            model.color.r,
            model.color.g,
            model.color.b,
            model.color.a
        )
    }
}