package org.yunghegel.salient.engine.api.asset.type

import com.badlogic.gdx.files.FileHandle
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.system.file.Filepath

class FileAsset(path: Filepath) : Asset<FileHandle>(path,FileHandle::class.java) {

    override val loader = object : Loader<FileHandle>() {
        override fun resolveHandle(assetHandle: AssetHandle): FileHandle {
            return FileHandle(assetHandle.path.path)
        }

        override fun load(assetHandle: AssetHandle): FileHandle {
            return resolveHandle(assetHandle)
        }
    }
}