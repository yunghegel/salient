package org.yunghegel.salient.engine.api.asset

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.files.FileHandle
import kotlinx.serialization.Transient
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.io.Filepath

abstract class Asset<T:Any>(val path : Filepath, type: Class<T>, params: AssetLoaderParameters<T>?=null) : AssetDescriptor<T>(path.handle,type,params) {


    @Transient val handle = AssetHandle(path.toString())

    val meta = Meta(handle.uuid,path.lastModified,path.size)

    data class Meta(val uuid: String, val lastModified: String, val size: Long)

    abstract val loader : Loader<T>

    fun load() : T {
        return loader.load(handle)
    }



    abstract inner class Loader<T> {

        abstract fun resolveHandle(assetHandle: AssetHandle) : FileHandle

        abstract fun load(assetHandle: AssetHandle) : T

    }
}