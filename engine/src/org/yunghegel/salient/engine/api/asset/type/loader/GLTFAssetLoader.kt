package org.yunghegel.salient.engine.api.asset.type.loader

import com.badlogic.gdx.files.FileHandle
import net.mgsx.gltf.data.GLTF
import net.mgsx.gltf.loaders.gltf.GLTFLoader
import net.mgsx.gltf.loaders.gltf.SeparatedDataFileResolver
import net.mgsx.gltf.loaders.shared.GLTFLoaderBase
import net.mgsx.gltf.scene3d.scene.SceneAsset

class GLTFAssetLoader(val file : FileHandle)  {

    private val dataFileResolver: SeparatedDataFileResolver = SeparatedDataFileResolver()

    data class GLTFAsset(val gltf: GLTF, val scene: SceneAsset)

    fun load() : GLTFAsset {
        val loader = GLTFLoader()
        dataFileResolver.load(file)
        val asset = loader.load(dataFileResolver, true)
        return GLTFAsset(dataFileResolver.root, asset)
    }

}