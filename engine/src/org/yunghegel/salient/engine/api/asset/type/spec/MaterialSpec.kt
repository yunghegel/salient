package org.yunghegel.salient.engine.api.asset.type.spec

import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.*
import kotlinx.serialization.Serializable
import net.mgsx.gltf.scene3d.attributes.ClippingPlaneAttribute
import net.mgsx.gltf.scene3d.attributes.FogAttribute
import net.mgsx.gltf.scene3d.attributes.MirrorAttribute
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute
import net.mgsx.gltf.scene3d.attributes.PBRFlagAttribute
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute
import kotlin.reflect.KClass

@Serializable
class MaterialSpec {

    var id: String = "<uninitialized>"
    var index: Int = -1
    var usage: Array<String> = arrayOf()
    var attributes: Array<AttributeSpec> = arrayOf()


    companion object {


            val colorAttributeAliases = listOf(
                "blended",
                "diffuseColor",
                "specularColor",
                "emissiveColor",
                "ambientColor",
                "reflectionColor",
                "fogColor",
                "ambientLightColor",
            ) to ColorAttribute::class
            val cupemapAliases = listOf("environmentCubemap") to CubemapAttribute::class
            val depthTestAliases = listOf("depthStencil") to DepthTestAttribute::class
            val floatAliases = listOf(
                "shininess",
                "alphaTest"
            ) to FloatAttribute::class
            val intAliases = listOf("culllface") to IntAttribute::class
            val textureAliases = listOf(
                "diffuseTexture",
                "specularTexture",
                "emissiveTexture",
                "ambientTexture",
                "reflectionTexture",
                "bumpTexture",
                "normalTexture"
            ) to TextureAttribute::class
            val clippingAliases = listOf("clippingPane") to ClippingPlaneAttribute::class
            val fogAliases = listOf("fogEquation") to FogAttribute::class
            val mirrorAliases = listOf("specularMirror") to MirrorAttribute::class

            val pbrColorAliases = listOf("BaseColorFactor") to PBRColorAttribute::class
            val pbrCubemapAliases = listOf(
                "DiffuseEnvSampler",
                "SpecularEnvSampler"
            ) to PBRCubemapAttribute::class
            val pbrFlagAliases = listOf("unlit") to PBRFlagAttribute::class
            val pbrFloatAliases = listOf(
                "Metallic",
                "Roughness",
                "NormalScale",
                "OcclusionStrength",
                "ShadowBias",
                "EmissiveIntensity",
                "TransmissionFactor",
                "IOR",
                "SpecularFactor"
            ) to PBRFloatAttribute::class



        fun fromMaterial(material: Material): MaterialSpec {
            val spec = MaterialSpec()
            spec.id = material.id
            material.forEach { attr ->
                spec.attributes += AttributeSpec.fromAttribute(attr)
            }
            return spec
        }

        fun toMaterial(spec: MaterialSpec): Material {
            val material = Material()
            material.id = spec.id
            spec.attributes.forEach { attr ->
                val attribute = AttributeSpec.toAttribute(attr)
                material.set(attribute)
            }
            return material
        }

    }

}