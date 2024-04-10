package org.yunghegel.salient.editor.app.dto.datatypes

import com.badlogic.gdx.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class Vector4Data(val x: Float, val y: Float, val z: Float, val w: Float) {

    fun toColor(): Color {
        return Color(x, y, z, w)
    }

    companion object {

        val zero = Vector4Data(0f, 0f, 0f, 0f)

    }
}