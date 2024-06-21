package org.yunghegel.salient.engine.api.asset.type.spec

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.attributes.*
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor
import kotlinx.serialization.Serializable
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute
import org.yunghegel.salient.engine.api.dto.datatypes.ColorData

@Serializable
sealed class AttributeSpec {
    var type : Long = 0L
    var alias : String = "<uninitialized>"

    companion object {
        fun fromAttribute(attribute: Attribute) : AttributeSpec {
            val attr = when(attribute) {
                is FloatAttribute -> {
                    if (attribute is PBRFloatAttribute) {
                        PBRFloatAttributeSpec().apply { value = attribute.value }
                    } else FloatAttributeSpec().apply { value = attribute.value }
                }
                is ColorAttribute ->{
                    if (attribute is PBRColorAttribute) {
                        PBRColorAttributeSpec().apply { color = ColorData.fromColor(attribute.color) }
                    } else ColorAttributeSpec().apply { color = ColorData.fromColor(attribute.color) }
                }
                is IntAttribute -> IntAttributeSpec().apply { value = attribute.value }
                is TextureAttribute -> TextureAttributeSpec()
                is BlendingAttribute -> BlendAttributeSpec().apply {
                    sourceFunction = attribute.sourceFunction
                    destinationFunction = attribute.destFunction
                    blended = attribute.blended
                    opacity = attribute.opacity
                }

                else -> {
                    throw IllegalArgumentException("Unsupported attribute type: ${attribute.javaClass}")
                }
            }
            attr.alias = Attribute.getAttributeAlias(attribute.type)
            attr.type = attribute.type
            return attr

        }
        fun toAttribute(spec: AttributeSpec) : Attribute {
            return when(spec) {
                is FloatAttributeSpec -> FloatAttributeSpec.toFloatAttribute(spec)
                is ColorAttributeSpec -> ColorAttributeSpec.toColorAttribute(spec)
                is IntAttributeSpec -> IntAttributeSpec.toIntAttribute(spec)
                is TextureAttributeSpec -> TextureAttribute(spec.type)
                is BlendAttributeSpec -> BlendAttributeSpec.toBlendAttribute(spec)
                is PBRColorAttributeSpec -> PBRColorAttributeSpec.toPBRColorAttr(spec)
                is PBRFloatAttributeSpec -> PBRFloatAttributeSpec.toPBRFloatAttr(spec)
            }
        }

    }

}
@Serializable
class FloatAttributeSpec : AttributeSpec() {
    var value : Float = 0f
    companion object {
        fun toFloatAttribute(spec: FloatAttributeSpec) : FloatAttribute {
            return when(spec.alias) {
                "shininess" -> FloatAttribute.createShininess(spec.value)
                "alphaTest" -> FloatAttribute.createAlphaTest(spec.value)
                "Metallic" -> PBRFloatAttribute.createMetallic(spec.value) as FloatAttribute
                "Roughness" -> PBRFloatAttribute.createRoughness(spec.value) as FloatAttribute
                "NormalScale" -> PBRFloatAttribute.createNormalScale(spec.value) as FloatAttribute
                "OcclusionStrength" -> PBRFloatAttribute.createOcclusionStrength(spec.value) as FloatAttribute
                "SpecularFactor" -> PBRFloatAttribute.createSpecularFactor(spec.value) as FloatAttribute
                else -> FloatAttribute(spec.type, spec.value)
            }
        }
    }
}
@Serializable
class ColorAttributeSpec : AttributeSpec() {
    var color : ColorData = ColorData()
    companion object {
        fun toColorAttribute(spec: ColorAttributeSpec) : ColorAttribute {
            return when(spec.alias) {
                "diffuseColor" -> ColorAttribute.createDiffuse(spec.color.toColor())
                "emissiveColor" -> ColorAttribute.createEmissive(spec.color.toColor())
                "ambientColor" -> ColorAttribute.createAmbient(spec.color.toColor())
                "reflectionColor" -> ColorAttribute.createReflection(spec.color.toColor())
                "specularColor" -> ColorAttribute.createSpecular(spec.color.toColor())
                "fogColor" -> ColorAttribute.createFog(spec.color.toColor())
                "ambientLightColor" -> ColorAttribute.createAmbientLight(spec.color.toColor())
                "BaseColorFactor" -> PBRColorAttribute.createBaseColorFactor(spec.color.toColor()) as ColorAttribute
                else -> ColorAttribute(spec.type, spec.color.toColor())
            }
        }
    }
}
@Serializable
class IntAttributeSpec : AttributeSpec() {
    var value : Int = 0
    companion object {
        fun toIntAttribute(spec: IntAttributeSpec) : IntAttribute {
            return when(spec.alias) {
                "cullFace" -> IntAttribute.createCullFace(spec.value)
                else -> IntAttribute(spec.type, spec.value)
            }
        }
    }
}
@Serializable
class BlendAttributeSpec : AttributeSpec() {
    var sourceFunction : Int = 0
    var destinationFunction : Int = 0
    var blended : Boolean = false
    var opacity : Float = 0f
    companion object {
        fun toBlendAttribute(spec: BlendAttributeSpec) : BlendingAttribute {
            return BlendingAttribute(spec.blended, spec.sourceFunction, spec.destinationFunction, spec.opacity)
        }
    }
}
@Serializable
class TextureAttributeSpec : AttributeSpec() {

    var path : String = "<uninitialized>"
    var uuid : String = "<uninitialized>"

    var offsetU : Float = 0f
    var offsetV : Float = 0f
    var scaleU : Float = 0f
    var scaleV : Float = 0f

    var uvIndex : Int = 0

    companion object {
        fun toTextureAttribute(spec: TextureAttributeSpec) : TextureAttribute {
            return TextureAttribute(spec.type, TextureDescriptor<Texture>(), spec.offsetU, spec.offsetV, spec.scaleU, spec.scaleV, spec.uvIndex)
        }
    }

}
@Serializable
class PBRColorAttributeSpec : AttributeSpec() {
    var color : ColorData = ColorData()
    companion object {
       fun toPBRColorAttr(spec: PBRColorAttributeSpec) : PBRColorAttribute {
           return when(spec.alias) {
               "BaseColorFactor" -> PBRColorAttribute.createBaseColorFactor(spec.color.toColor())
               else -> PBRColorAttribute(spec.type, spec.color.toColor())
           }
        }
    }
}
@Serializable
class PBRFloatAttributeSpec : AttributeSpec() {
    var value : Float = 0f
    companion object {
        fun toPBRFloatAttr(spec: PBRFloatAttributeSpec) : PBRFloatAttribute {
            return when(spec.alias) {
                "Metallic" -> PBRFloatAttribute.createMetallic(spec.value)
                "Roughness" -> PBRFloatAttribute.createRoughness(spec.value)
                "NormalScale" -> PBRFloatAttribute.createNormalScale(spec.value)
                "OcclusionStrength" -> PBRFloatAttribute.createOcclusionStrength(spec.value)
                "SpecularFactor" -> PBRFloatAttribute.createSpecularFactor(spec.value)
                "ShadowBias" -> PBRFloatAttribute(PBRFloatAttribute.ShadowBias, spec.value)
                "EmissiveIntensity" -> PBRFloatAttribute(PBRFloatAttribute.EmissiveIntensity, spec.value)
                "TransmissionFactor" -> PBRFloatAttribute.createTransmissionFactor(spec.value)
                "IOR" -> PBRFloatAttribute.createIOR(spec.value)
                else -> PBRFloatAttribute(spec.type, spec.value)
            } as PBRFloatAttribute
        }
    }
}

