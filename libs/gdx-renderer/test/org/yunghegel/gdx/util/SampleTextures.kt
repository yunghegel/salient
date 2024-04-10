package org.yunghegel.gdx.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute

enum class SampleTextures(val texture: String) {

    AMBIENT_OCCLUSION("AmbientOcclusion.png"),
    DIFFUSE("Diffuse.png"),
    NORMAL("Normal.png"),
    SPECULAR("Specular.png");

    fun load(basePath: String = "textures/") : Texture {
        val path = "${basePath}${texture}"
        return Texture(Gdx.files.internal(path))
    }

    fun attach(material: Material) {
        val texture = load()
        val attr = when(this) {
            AMBIENT_OCCLUSION -> TextureAttribute.createAmbient(texture)
            DIFFUSE -> TextureAttribute.createDiffuse(texture)
            NORMAL -> TextureAttribute.createNormal(texture)
            SPECULAR -> TextureAttribute.createSpecular(texture)
        }
        material.set(attr)
    }

    companion object {
        fun attachEach(material:Material, vararg textures: SampleTextures) {
            textures.forEach { it.attach(material) }
        }
    }

}