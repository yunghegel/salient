package org.yunghegel.salient.engine.api.scene

import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute
import net.mgsx.gltf.scene3d.attributes.FogAttribute
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx
import org.yunghegel.salient.engine.api.dto.SceneEnvironmentDTO

abstract class SceneEnvironment : Environment() {
    companion object {

        fun applyDTO(env: SceneEnvironment, data: SceneEnvironmentDTO) {
            val fog = data.fogData.fromDTO(data.fogData)
            val ambient = data.ambientLightData.fromDTO(data.ambientLightData)
            val directionalLight = data.directionalLightData.fromDTO(data.directionalLightData)
            env.set(fog)
            env.set(ambient)
            env.add(directionalLight)
        }

        fun toDTO(model: Environment): SceneEnvironmentDTO {
            val dto = SceneEnvironmentDTO()
            if (model.has(FogAttribute.FogEquation)) {
                val fog = model.get(FogAttribute.FogEquation) as FogAttribute
                dto.fogData = dto.fogData.toDTO(fog)
            }
            if (model.has(ColorAttribute.AmbientLight)) {
                val ambient = model.get(ColorAttribute.AmbientLight) as ColorAttribute
                dto.ambientLightData = dto.ambientLightData.toDTO(ambient)
            }
            if (model.has(DirectionalLightsAttribute.Type)) {
                val directional = model.get(DirectionalLightsAttribute::class.java, DirectionalLightsAttribute.Type)
                if (directional==null) return@toDTO dto
                val light = directional.lights.filterIsInstance<DirectionalLightEx>().first()
                if (light !=null) dto.directionalLightData = dto.directionalLightData.toDTO(light)
            }
            return dto
        }
    }
}