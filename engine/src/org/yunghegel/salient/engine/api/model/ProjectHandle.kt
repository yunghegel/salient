package org.yunghegel.salient.engine.api.model

import kotlinx.serialization.Serializable
import org.yunghegel.salient.engine.api.*
import org.yunghegel.salient.engine.api.ID.Companion.generateId
import org.yunghegel.salient.engine.io.Filepath
import org.yunghegel.salient.engine.io.Paths




@Serializable
class ProjectHandle(override val name:String, override val path: Filepath) : NamedObjectResource, Default<ProjectHandle> {



    override val uuid: String = generateUUID()

    override val id: Long = generateId()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProjectHandle) return false

        if (name != other.name) return false
        if (path != other.path) return false
        if (uuid != other.uuid) return false
        if (id != other.id) return false

        return true
    }

    override fun createDefault(): ProjectHandle {
        return ProjectHandle("default", Paths.PROJECT_DIR_FOR("default"))
    }

    companion object : Default<ProjectHandle> {
        override fun createDefault(): ProjectHandle {
            return ProjectHandle("default", Paths.PROJECT_DIR_FOR("default"))
        }
    }
    
    

}