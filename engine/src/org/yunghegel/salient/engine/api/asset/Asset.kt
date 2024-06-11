package org.yunghegel.salient.engine.api.asset

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.files.FileHandle
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.system.file.Filepath

abstract class Asset<T:Any>(val path : Filepath, type: Class<T>, var handle:AssetHandle=AssetHandle(path.toString()),params: AssetLoaderParameters<T>?=null) : AssetDescriptor<T>(path.handle,type,params) {


    var loaded = false
        private set

    var value : T? = null

    val meta = Meta(handle.uuid,path.lastModified,path.size)

    data class Meta(val uuid: String, val lastModified: String, val size: Long)

    abstract val loader : Loader<T>

    fun load() : T {

        loaded = true
        value = loader.load(handle)
        return value!!
    }



    abstract inner class Loader<T> {

        abstract fun resolveHandle(assetHandle: AssetHandle) : FileHandle

        abstract fun load(assetHandle: AssetHandle) : T

    }
}