@file:OptIn(ExperimentalStdlibApi::class)

package org.yunghegel.salient.engine.api.model

import com.badlogic.gdx.files.FileHandle
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.gdx.utils.data.ID.Companion.generateUUID
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.salient.engine.api.asset.Extras
import org.yunghegel.salient.engine.api.properties.Resource
import org.yunghegel.salient.engine.system.file.FileType
import org.yunghegel.salient.engine.system.file.Filepath

@Serializable
open class AssetHandle(@Transient val pth: String="", @Transient private val _uuid: String = generateUUID(), @Transient val _extras: Map<String,String> = mapOf()) : Resource, ID, Named {

    constructor(handle:FileHandle) : this(handle.path())

    override val id: Int = generateID()

    override val file: Filepath = Filepath(pth)

    override val uuid: String = _uuid

    override val name: String = file.name

    val type : String = resolveType(file.extension)

    val extras : Extras = Extras()

    init {
        _extras.forEach { (key, value) -> extras[key] = value }
    }

    fun resolveType(ext: String) : String {
        FileType.entries.forEach { type ->
            if (type.extensions.contains(ext)) {
                return type.name
            }
        }

        return "unknown"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AssetHandle) return false

        if (file != other.file) return false

        if (uuid != other.uuid) return false

        return true
    }

    override fun toString(): String {
        return "AssetHandle[$name, $file, $uuid, $type, $extras]"
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + pth.hashCode()
        result = 31 * result + _uuid.hashCode()
        result = 31 * result + _extras.hashCode()
        result = 31 * result + file.hashCode()
        result = 31 * result + uuid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + extras.hashCode()
        return result
    }

}