package org.yunghegel.salient.engine.api.asset

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.files.FileHandle
import ktx.assets.async.AssetStorage
import org.yunghegel.gdx.utils.data.Searchable
import org.yunghegel.salient.engine.api.asset.type.AssetType
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.helpers.save
import org.yunghegel.salient.engine.system.file.FileType
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.inject

abstract class Asset<T:Any>(val path : Filepath, type: Class<T>, var handle:AssetHandle=AssetHandle(path.toString()),params: AssetLoaderParameters<T>?=null)
    : AssetDescriptor<T>(path.handle,type,params), Searchable {

    override val searchTerms: List<String> = listOf(type.name, handle.name,handle.type, handle.pth)

    val storage : AssetStorage by lazy { inject() }

    var loaded = false
        private set

    var value : T? = null

    val meta = Meta(handle.uuid,path.lastModified,path.size)

    val usages = Usages()

    data class Meta(val uuid: String, val lastModified: String, val size: Long)

    data class Usages(val uuids: MutableList<String> = mutableListOf())

    val filetype: FileType = FileType.parse(path.extension)

    val assetType : AssetType = AssetType.fromFiletype(filetype)

    abstract val loader : Loader<T>

    fun load() : T {
        loaded = true
        value = loader.load(handle)
        return value!!
    }

    fun export(path: String = Filepath.toString(),content: String) {
        save(path) {content}
    }



    abstract inner class Loader<T> {

        abstract fun resolveHandle(assetHandle: AssetHandle) : FileHandle

        abstract fun load(assetHandle: AssetHandle) : T

    }
}