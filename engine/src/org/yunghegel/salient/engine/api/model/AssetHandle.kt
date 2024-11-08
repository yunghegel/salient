@file:OptIn(ExperimentalStdlibApi::class)

package org.yunghegel.salient.engine.api.model

import com.badlogic.gdx.files.FileHandle
import kotlinx.serialization.Serializable
import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.salient.engine.api.properties.Resource
import org.yunghegel.salient.engine.system.file.FileType
import org.yunghegel.salient.engine.system.file.Filepath

@Serializable
class AssetHandle(val pth: String="") : Resource, ID, Named {

    constructor(handle:FileHandle) : this(handle.path())

    override val id: Int = generateID()

    override val path: Filepath = Filepath(pth)

    override val uuid: String = generateUUID()

    override val name: String = path.name

    val type : String = resolveType(path.extension)

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

        if (path != other.path) return false

        if (uuid != other.uuid) return false

        return true
    }

    override fun toString(): String {
        return "AssetHandle[$name, $path, $uuid, $type]"
    }

}