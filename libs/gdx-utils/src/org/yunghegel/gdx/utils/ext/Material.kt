package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute

fun diffuse(tex: Texture) : TextureAttribute {
    return TextureAttribute(TextureAttribute.Diffuse,tex)
}

fun normal(tex: Texture) : TextureAttribute {
    return TextureAttribute(TextureAttribute.Normal,tex)
}

fun specular(tex: Texture) : TextureAttribute {
    return TextureAttribute(TextureAttribute.Specular,tex)
}

fun emissive(tex: Texture) : TextureAttribute {
    return TextureAttribute(TextureAttribute.Emissive,tex)
}

fun ambient(tex: Texture) : TextureAttribute {
    return TextureAttribute(TextureAttribute.Ambient,tex)
}

fun shininess(value: Float) : FloatAttribute {
    return FloatAttribute(FloatAttribute.Shininess,value)
}

fun alpha(value: Float) : FloatAttribute {
    return FloatAttribute(FloatAttribute.AlphaTest,value)
}

fun reflection(tex : Texture) : TextureAttribute {
    return TextureAttribute(TextureAttribute.Reflection,tex)
}

fun pbrDiffuse(tex: Texture) : PBRTextureAttribute {
    return PBRTextureAttribute(PBRTextureAttribute.Diffuse,tex)
}

fun pbrNormal(tex: Texture) : PBRTextureAttribute {
    return PBRTextureAttribute(PBRTextureAttribute.Normal,tex)
}

fun pbrSpecular(tex: Texture) : PBRTextureAttribute {
    return PBRTextureAttribute(PBRTextureAttribute.Specular,tex)
}

fun pbrEmissive(tex: Texture) : PBRTextureAttribute {
    return PBRTextureAttribute(PBRTextureAttribute.Emissive,tex)
}

fun pbrAmbient(tex: Texture) : PBRTextureAttribute {
    return PBRTextureAttribute(PBRTextureAttribute.Ambient,tex)
}

fun pbrReflection(tex: Texture) : PBRTextureAttribute {
    return PBRTextureAttribute(PBRTextureAttribute.Reflection,tex)
}


fun toPBR(textureAttribute: TextureAttribute) : PBRTextureAttribute {
    return when(textureAttribute.type) {
        TextureAttribute.Diffuse -> pbrDiffuse(textureAttribute.textureDescription.texture)
        TextureAttribute.Normal -> pbrNormal(textureAttribute.textureDescription.texture)
        TextureAttribute.Specular -> pbrSpecular(textureAttribute.textureDescription.texture)
        TextureAttribute.Emissive -> pbrEmissive(textureAttribute.textureDescription.texture)
        TextureAttribute.Ambient -> pbrAmbient(textureAttribute.textureDescription.texture)
        TextureAttribute.Reflection -> pbrReflection(textureAttribute.textureDescription.texture)
        else -> throw IllegalArgumentException("Unknown texture attribute kind: ${textureAttribute.type}")
    }
}

fun convertToPBR(material: Material) {
    material.forEach { attr ->
        if (attr is TextureAttribute) {
            val pbr = toPBR(attr)
            println("Converted $attr to $pbr")
            material.remove(attr.type)
            material.set(pbr)
        }
    }
}

fun textureAttr(kind: String,tex: Texture) : TextureAttribute {
    return when(kind.lowercase()) {
        "diffuse" -> diffuse(tex)
        "normal" -> normal(tex)
        "specular" -> specular(tex)
        "emissive" -> emissive(tex)
        "ambient" -> ambient(tex)
        "reflection" -> reflection(tex)
        else -> throw IllegalArgumentException("Unknown texture attribute kind: $kind")
    }
}

fun scale(mat:Material,kind:String, uvx:Float, uvy: Float, scalex: Float,scaley:Float) {
    val attr = textureAttr(kind,mat)
    attr?.let { attribute ->
        attribute.scaleU = uvx
        attribute.scaleV = uvy
        attribute.offsetU = scalex
        attribute.offsetV = scaley
    }
}

fun textureAttr(kind: String, mat: Material) : TextureAttribute? {
    return when (kind.lowercase()) {
        "diffuse" -> mat.get(TextureAttribute.Diffuse) as TextureAttribute?
        "normal" -> mat.get(TextureAttribute.Normal) as TextureAttribute?
        "specular" -> mat.get(TextureAttribute.Specular) as TextureAttribute?
        "emissive" -> mat.get(TextureAttribute.Emissive) as TextureAttribute?
        "ambient" -> mat.get(TextureAttribute.Ambient) as TextureAttribute?
        "reflection" -> mat.get(TextureAttribute.Reflection) as TextureAttribute?
        else -> throw IllegalArgumentException("Unknown texture attribute kind: $kind")
    }
}

fun pbrTexture(kind: String,tex: Texture) : PBRTextureAttribute {
    return when(kind.lowercase()) {
        "diffuse" -> pbrDiffuse(tex)
        "normal" -> pbrNormal(tex)
        "specular" -> pbrSpecular(tex)
        "emissive" -> pbrEmissive(tex)
        "ambient" -> pbrAmbient(tex)
        "reflection" -> pbrReflection(tex)
        else -> throw IllegalArgumentException("Unknown texture attribute kind: $kind")
    }
}

fun Material.pbrGet(kind: String,tex: Texture) : PBRTextureAttribute? {
    return pbrTexture(kind, tex)
}

fun Material.pbrSet(kind: String,tex: Texture) {
    set(pbrTexture(kind,tex))
}

operator fun Material.get(kind: String) : TextureAttribute? {
    return textureAttr(kind,this)
}

operator fun Material.set(kind: String,tex: Texture) {
    set(textureAttr(kind,tex))
}



