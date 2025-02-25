package org.yunghegel.salient.engine.api.model

import com.badlogic.gdx.files.FileHandle
import kotlinx.serialization.Serializable
import org.yunghegel.salient.engine.api.Identifiable
import org.yunghegel.salient.engine.api.properties.NamedObjectResource
import org.yunghegel.salient.engine.system.file.Filepath
import kotlin.io.path.nameWithoutExtension

@Serializable
open class Handle(
    val path: String,
    val type: HandleType = HandleType.resolveType(path.substringAfterLast('.')),
    override val id: Int = type.nextId()
) : NamedObjectResource {

    override val file: Filepath = Filepath(path)

    override val name: String = file.nameWithoutExtension

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Handle) return false

        if (path != other.path) return false

        if (id != other.id) return false

        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + path.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + file.hashCode()
        return result
    }

}