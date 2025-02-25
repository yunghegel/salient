package org.yunghegel.salient.engine.api.asset.type.loader

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import net.mgsx.gltf.data.GLTF
import net.mgsx.gltf.data.texture.GLTFImage
import net.mgsx.gltf.data.texture.GLTFNormalTextureInfo
import net.mgsx.gltf.data.texture.GLTFOcclusionTextureInfo
import net.mgsx.gltf.data.texture.GLTFTextureInfo
import net.mgsx.gltf.loaders.gltf.GLTFLoader
import net.mgsx.gltf.loaders.gltf.SeparatedDataFileResolver
import net.mgsx.gltf.loaders.shared.GLTFLoaderBase
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute
import net.mgsx.gltf.scene3d.scene.SceneAsset
import org.checkerframework.checker.units.qual.m

class GLTFAssetLoader(val file : FileHandle)  {

    private val dataFileResolver: SeparatedDataFileResolver = SeparatedDataFileResolver()

    lateinit var gltf: GLTF

    private val materialImageMap = mutableMapOf<Int,GLTFTextureInfo>()

    private val usageMap = mutableMapOf<Int,Long>()

    val textureMap : MutableMap<GLTFTextureInfo,FileHandle> = mutableMapOf()

    data class GLTFAsset(val gltf: GLTF, val scene: SceneAsset)

    fun load() : GLTFAsset {
        val loader = GLTFLoader()
        dataFileResolver.load(file)
        val asset = loader.load(dataFileResolver, true)
        gltf = dataFileResolver.root
        populateMaterialImageMap()
        materialImageMap.forEach({ (key,value) ->
//            val image = getImage(key)
//            image?.let { i ->
//                val path = resolveImagePath(i)
//                println("Image $key: $path")
//                val file = FileHandle(path)
//                 if (!file.exists()) {
//                    println("File does not exist: $path")
//                } else {
//                    println("File exists: $path")
//                }
//                textureMap[value] = file
//            }
        })
        return GLTFAsset(dataFileResolver.root, asset)
    }

    fun populateMaterialImageMap() {
        gltf.materials.forEach { material ->
            val textures = listOf(material.emissiveTexture, material.normalTexture, material.occlusionTexture, material.pbrMetallicRoughness.baseColorTexture, material.pbrMetallicRoughness.metallicRoughnessTexture).filterNotNull()
            textures.forEach { textureInfo ->
                materialImageMap[textureInfo.index] = textureInfo
                when(textureInfo) {
                    is GLTFNormalTextureInfo -> {
                        usageMap[textureInfo.index] = PBRTextureAttribute.Normal
                    }
                    is GLTFOcclusionTextureInfo -> {
                        usageMap[textureInfo.index] = PBRTextureAttribute.OcclusionTexture
                    }
                    else -> {
                        usageMap[textureInfo.index] = PBRTextureAttribute.EmissiveTexture
                    }
                }
            }
        }
        materialImageMap.forEach { (key,value) ->
            println("Material $key: ${value.index}")
        }
    }

    fun getImage(index: Int) : GLTFImage? {
        return gltf.images[materialImageMap[index]!!.index]
    }

    fun resolveImagePath(image: GLTFImage) : String {
        val basepath = file.parent().path()
        return "$basepath/${image.uri}"
    }


}