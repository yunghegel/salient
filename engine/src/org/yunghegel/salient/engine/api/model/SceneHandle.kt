package org.yunghegel.salient.engine.api.model

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.yunghegel.salient.engine.api.NamedObjectResource
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.helpers.save
import org.yunghegel.salient.engine.io.Filepath
import org.yunghegel.salient.engine.io.Paths

@Serializable
class SceneHandle(override val name: String, override val path: Filepath, @Transient val serializerid: Long? = null, @Transient val serialuuid: String?=null, @Transient val proj: EditorProject<*,*>? = null) : NamedObjectResource {

    init {
        proj?.indexScene(this)
    }

    override val id: Long = serializerid ?: generateID()

    override val uuid: String = serialuuid ?: generateUUID()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SceneHandle) return false

        if (name != other.name) return false
        if (path != other.path) return false
        if (serializerid != other.serializerid) return false
        if (serialuuid != other.serialuuid) return false
        if (proj != other.proj) return false
        if (id != other.id) return false
        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + uuid.hashCode()
        return result
    }

    companion object {

        fun loadFromFile(path: Filepath, proj: EditorProject<*,*>) : SceneHandle {
            val string = Paths.SCENE_INDEX_FILEPATH_FOR(proj.name,path.handle.nameWithoutExtension())
            println(string.path)
            val handle = Yaml.default.decodeFromString<SceneHandle>(SceneHandle.serializer(), string.readString)
            return handle
        }

        fun saveToFile(handle: SceneHandle,editorProj: EditorProject<*,*>) {
            val data = Yaml.default.encodeToString(SceneHandle.serializer(), handle)
            val filepath = Paths.SCENE_INDEX_FILEPATH_FOR(editorProj.name,handle.name)
            filepath.mkfile()
            save(filepath.path) { data }
            println("SERIALIZED SCENE INDEX:\n$data")
        }



    }


}