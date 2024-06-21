package org.yunghegel.salient.editor.asset

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.utils.Array
import org.yunghegel.salient.engine.api.asset.type.spec.MaterialSpec

class MaterialLoader(resolver: FileHandleResolver =InternalFileHandleResolver()) : AsynchronousAssetLoader<Material, MaterialLoader.MaterialParameter>(resolver){



    class MaterialParameter : AssetLoaderParameters<Material>()

    override fun getDependencies(
        fileName: String?,
        file: FileHandle?,
        parameter: MaterialParameter?
    ): Array<AssetDescriptor<Any>>? {
        return null
    }

    override fun loadAsync(
        manager: AssetManager?,
        fileName: String?,
        file: FileHandle?,
        parameter: MaterialParameter?
    ) {

    }

    override fun loadSync(
        manager: AssetManager?,
        fileName: String?,
        file: FileHandle?,
        parameter: MaterialParameter?
    ): Material {
        TODO("Not yet implemented")
    }

}