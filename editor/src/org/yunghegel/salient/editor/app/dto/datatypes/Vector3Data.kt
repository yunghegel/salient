package org.yunghegel.salient.engine.api.model.dto.datatypes

import com.badlogic.gdx.math.Vector3
import kotlinx.serialization.Serializable

@Serializable
data class Vector3Data(val x: Float, val y: Float, val z: Float) {

    fun toVec3(): Vector3 {
        return Vector3(x, y, z)
    }

    companion object {

        val zero = Vector3Data(0f, 0f, 0f)
        fun fromVec3(vector: Vector3): Vector3Data {
            return Vector3Data(vector.x, vector.y, vector.z)
        }

        fun toVec3(vector: Vector3Data): Vector3 {
            return Vector3(vector.x, vector.y, vector.z)
        }

    }
}