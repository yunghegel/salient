package org.yunghegel.salient.engine.api.asset.type

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Material
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.system.file.Filepath

class MaterialAsset(path: Filepath) : Asset<Material>(path, Material::class.java) {

    override val loader = MaterialLoader()

    inner class MaterialLoader : Loader<Material>() {

        override fun resolveHandle(assetHandle: AssetHandle): FileHandle {
            return assetHandle.path.handle
        }

        override fun load(assetHandle: AssetHandle): Material {
            val file = resolveHandle(assetHandle)
            return Material()
        }

    }

}