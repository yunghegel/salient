package org.yunghegel.salient.editor.app.dto.datatypes

import com.badlogic.gdx.math.Vector2
import kotlinx.serialization.Serializable

@Serializable
data class Vector2Data(val x: Float, val y: Float) {

    companion object {

        val zero = Vector2Data(0f, 0f)
        fun fromVec2(vector: Vector2): Vector2Data {
            return Vector2Data(vector.x, vector.y)
        }

        fun toVec2(vector: Vector2Data): Vector2 {
            return Vector2(vector.x, vector.y)
        }
    }
}