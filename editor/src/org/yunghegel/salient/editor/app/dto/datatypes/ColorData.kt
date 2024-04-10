package org.yunghegel.salient.engine.api.model.dto.datatypes

import com.badlogic.gdx.graphics.*
import kotlinx.serialization.*

@Serializable
data class ColorData(val r: Float, val g: Float, val b: Float, val a: Float) {

    companion object {

        val white = ColorData(1f, 1f, 1f, 1f)
        val black = ColorData(0f, 0f, 0f, 1f)
        val red = ColorData(1f, 0f, 0f, 1f)
        val green = ColorData(0f, 1f, 0f, 1f)
        val blue = ColorData(0f, 0f, 1f, 1f)
        val yellow = ColorData(1f, 1f, 0f, 1f)
        val cyan = ColorData(0f, 1f, 1f, 1f)
        val magenta = ColorData(1f, 0f, 1f, 1f)

        fun fromColor(color: Color): ColorData {
            return ColorData(color.r, color.g, color.b, color.a)
        }

        fun toColor(color: ColorData): Color {
            return Color(color.r, color.g, color.b, color.a)
        }
    }
}
