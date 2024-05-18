package org.yunghegel.salient.engine.api.model

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.yunghegel.salient.engine.api.properties.NamedObjectResource
import org.yunghegel.salient.engine.api.project.EditorProject
import org.yunghegel.salient.engine.helpers.save
import org.yunghegel.salient.engine.system.file.Filepath
import org.yunghegel.salient.engine.system.file.Paths

@Serializable
open class SceneHandle(override val name: String, override val path: Filepath, @Transient val serializerid: Int? = null, @Transient val serialuuid: String?=null, @Transient val proj: EditorProject<*,*>? = null) :
    NamedObjectResource {

    init {
        proj?.indexScene(this)
    }

    override val id: Int = serializerid ?: generateID()

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

    companion object {

        fun loadFromFile(path: Filepath, proj: EditorProject<*,*>) : SceneHandle {
            val string = Paths.SCENE_INDEX_FILEPATH_FOR(proj.name,path.handle.nameWithoutExtension())
            val handle = Yaml.default.decodeFromString<SceneHandle>(serializer(), string.readString)
            return handle
        }

        fun saveToFile(handle: SceneHandle,editorProj: EditorProject<*,*>) {
            val data = Yaml.default.encodeToString(serializer(), handle)
            val filepath = Paths.SCENE_INDEX_FILEPATH_FOR(editorProj.name,handle.name)
            filepath.mkfile()
            save(filepath.path) { data }
        }



    }


}