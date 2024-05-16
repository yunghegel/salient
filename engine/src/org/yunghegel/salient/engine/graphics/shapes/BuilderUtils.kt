package org.yunghegel.salient.engine.graphics.shapes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute

object BuilderUtils {
    fun buildMaterial(color: Color?): Material {
        val mat = Material()
        mat.set(PBRColorAttribute.createDiffuse(color))
        mat.set(PBRColorAttribute.createSpecular(color))
        mat.set(PBRColorAttribute.createAmbient(color))
        mat.set(PBRColorAttribute.createEmissive(color))
        mat.set(PBRColorAttribute.createBaseColorFactor(color))
        mat.set(ColorAttribute.createDiffuse(color))
        mat.set(ColorAttribute.createSpecular(color))
        mat.set(ColorAttribute.createAmbient(color))
        mat.set(ColorAttribute.createEmissive(color))
        mat.set(ColorAttribute.createReflection(color))
        return mat
    }

    fun buildAttributes(normal: Boolean, color: Boolean, texCoords: Boolean): Long {
        var attributes: Long = 0
        if (normal) attributes = attributes or VertexAttributes.Usage.Normal.toLong()
        if (color) attributes = attributes or VertexAttributes.Usage.ColorUnpacked.toLong()
        if (texCoords) attributes = attributes or VertexAttributes.Usage.TextureCoordinates.toLong()
        return attributes
    }

    fun buildAttributes(
        normal: Boolean,
        color: Boolean,
        texCoords: Boolean,
        tangent: Boolean,
        binormal: Boolean
    ): Long {
        var attributes: Long = 0
        if (normal) attributes = attributes or VertexAttributes.Usage.Normal.toLong()
        if (color) attributes = attributes or VertexAttributes.Usage.ColorUnpacked.toLong()
        if (texCoords) attributes = attributes or VertexAttributes.Usage.TextureCoordinates.toLong()
        if (tangent) attributes = attributes or VertexAttributes.Usage.Tangent.toLong()
        if (binormal) attributes = attributes or VertexAttributes.Usage.BiNormal.toLong()
        return attributes
    }

    fun getRandomColor(): Color {
            val color = Color()
            color.fromHsv(Math.random().toFloat() * 360, 1f, 1f)
            return color
    }

}
