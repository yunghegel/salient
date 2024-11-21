package org.yunghegel.salient.engine.api.asset

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.files.FileHandle
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ktx.assets.async.AssetStorage
import org.yunghegel.gdx.utils.data.Searchable
import org.yunghegel.salient.engine.api.asset.type.AssetType
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.helpers.Ignore
import org.yunghegel.salient.engine.helpers.SClass
import org.yunghegel.salient.engine.helpers.Serializer.yaml
import org.yunghegel.salient.engine.helpers.save
import org.yunghegel.salient.engine.system.file.FileType
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.inject

@Serializable
abstract class Asset<T:Any>(val path : Filepath, val type: SClass<T>, var handle:AssetHandle=AssetHandle(path.toString()),@Ignore val params: AssetLoaderParameters<T>?=null)
    :  Searchable {

    override val searchTerms: List<String> = listOf(type.name, handle.name,handle.type, handle.pth)

    @Transient
    val storage : AssetStorage = inject()

    var loaded = false
        private set

    @Transient
    open var value : T? = null

    val meta = Meta(handle.uuid,path.lastModified,path.size)

    val usages = Usages()

    @Serializable
    data class Meta(val uuid: String, val lastModified: String, val size: Long)

    @Serializable
    data class Usages(val uuids: MutableList<String> = mutableListOf()) {
        @Transient val depenciesToLoad = mutableListOf<AssetHandle>()
    }

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

    fun addDependency(assetHandle: AssetHandle) {
        usages.uuids.add(assetHandle.uuid)
        usages.depenciesToLoad.add(assetHandle)
    }

    abstract inner class Loader<T> {

        abstract fun resolveHandle(assetHandle: AssetHandle) : FileHandle

        abstract fun load(assetHandle: AssetHandle) : T

    }

    abstract inner class AssetFile
}