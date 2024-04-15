package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils

/**
 * Returns a color from HSV values.
 */
fun hsvToColor(color: Color, h: Float, s: Float, v: Float): Color {
    val x = (h / 60f + 6) % 6
    val i = MathUtils.floorPositive(x)
    val f = x - i
    val p = v * (1 - s)
    val q = v * (1 - s * f)
    val t = v * (1 - s * (1 - f))
    when (i) {
        0 -> {
            color.r = v
            color.g = t
            color.b = p
        }

        1 -> {
            color.r = q
            color.g = v
            color.b = p
        }

        2 -> {
            color.r = p
            color.g = v
            color.b = t
        }

        3 -> {
            color.r = p
            color.g = q
            color.b = v
        }

        4 -> {
            color.r = t
            color.g = p
            color.b = v
        }

        else -> {
            color.r = v
            color.g = p
            color.b = q
        }
    }
    return color
}


/**
 * Returns a color from HSV values.
 */
fun hsvToColor(c: Color, hsv: FloatArray): Color {
    return hsvToColor(c, hsv[0], hsv[1], hsv[2])
}

fun randomColor(lo: Float, hi: Float): Color {
    val col = Color()
    col.r = MathUtils.random(lo, hi)
    col.g = MathUtils.random(lo, hi)
    col.b = MathUtils.random(lo, hi)
    col.a = 1f
    return col
}

fun colorToHex(color: Color): String {
    val r = (color.r * 255).toInt()
    val g = (color.g * 255).toInt()
    val b = (color.b * 255).toInt()
    return String.format("#%02X%02X%02X", r, g, b)
}

fun hexToColor(hex: String): Color {
    val r = Integer.valueOf(hex.substring(1, 3), 16) / 255f
    val g = Integer.valueOf(hex.substring(3, 5), 16) / 255f
    val b = Integer.valueOf(hex.substring(5, 7), 16) / 255f
    return Color(r, g, b, 1f)
}

fun randomColor(): Color {
    return randomColor(0f, 1f)
}

fun rand(): Color {
    val saturatedColor = Color.WHITE.cpy()
    val color = hsvToColor(saturatedColor, MathUtils.random() * 360f, 1f, 1f)
    return color
}

fun Color.alpha(alpha: Float): Color {
    this.a = alpha
    return this
}