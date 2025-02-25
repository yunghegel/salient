package org.yunghegel.salient.engine.api

import org.yunghegel.salient.engine.api.ecs.ComponentCloneable.Companion.generateMessage
import org.yunghegel.salient.engine.api.properties.NamedObjectResource

data class RegistryConflict(
    val type: ConflictType,
    val conflict: NamedObjectResource,
    val existing: NamedObjectResource
) : Exception() {

    fun generateMessage(): String {
        return when (type) {
            ConflictType.NAME -> "Name conflict: ${conflict.name} already exists"
            ConflictType.PATH -> "Path conflict: ${conflict.file.path} already exists"
            ConflictType.ID -> "ID conflict: ${conflict.id} already exists"
        }
    }

    override val message: String = generateMessage()


    enum class ConflictType {
        NAME, PATH, ID
    }

    companion object {
        fun check(conflict: NamedObjectResource, existing: NamedObjectResource): RegistryConflict? {
            return when {
                conflict.name == existing.name -> RegistryConflict(ConflictType.NAME, conflict, existing)
                conflict.file.path == existing.file.path -> RegistryConflict(ConflictType.PATH, conflict, existing)
                conflict.id == existing.id -> RegistryConflict(ConflictType.ID, conflict, existing)
                else -> null
            }
        }
    }

}