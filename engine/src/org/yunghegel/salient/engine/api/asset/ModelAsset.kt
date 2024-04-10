package org.yunghegel.salient.engine.api.asset

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.io.Filepath

class ModelAsset(path: Filepath) : Asset<Model>(path, Model::class.java) {

    override val loader = ModelLoader()

    inner class ModelLoader : Loader<Model>() {

        override fun resolveHandle(assetHandle: AssetHandle): FileHandle {
            return assetHandle.path.handle
        }

        override fun load(assetHandle: AssetHandle): Model {
            val file = resolveHandle(assetHandle)
            return ObjLoader().loadModel(file)
        }

    }

}