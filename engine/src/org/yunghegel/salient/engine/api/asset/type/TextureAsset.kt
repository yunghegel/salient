package org.yunghegel.salient.engine.api.asset.type

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Texture
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.system.file.Filepath

class TextureAsset(path: Filepath) : Asset<Texture>(path,Texture::class.java) {

        override val loader = TextureLoader()

        inner class TextureLoader : Loader<Texture>() {

            override fun resolveHandle(assetHandle: AssetHandle): FileHandle {
                return assetHandle.file.handle
            }

            override fun load(assetHandle: AssetHandle): Texture {
                val file = resolveHandle(assetHandle)
                return Texture(file)
            }

        }
}