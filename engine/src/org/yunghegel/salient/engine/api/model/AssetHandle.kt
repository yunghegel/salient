package org.yunghegel.salient.engine.api.model

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import kotlinx.serialization.Serializable
import org.yunghegel.salient.engine.api.ID
import org.yunghegel.salient.engine.api.Named
import org.yunghegel.salient.engine.api.Resource
import org.yunghegel.salient.engine.io.FileType
import org.yunghegel.salient.engine.io.Filepath

@Serializable
class AssetHandle(val pth: String) : Resource,ID,Named {

    override val id: Long = generateID()

    override val path: Filepath = Filepath(pth)

    override val uuid: String = generateUUID()

    override val name: String = path.name

    val type : String = resolveType(path.extension)

    fun toClass() : Class<*> = Class.forName(type)


    fun resolveType(ext: String) : String {
        if (FileType.MODEL.extensions.contains(ext)) {
            return Model::class.java.typeName
        }
        if (FileType.TEXTURE.extensions.contains(ext)) {
            return Texture::class.java.typeName
        }
        return "unknown"
    }

    override fun toString(): String {
        return "Asset handle: [$name, $path, $uuid, $type]"
    }

}