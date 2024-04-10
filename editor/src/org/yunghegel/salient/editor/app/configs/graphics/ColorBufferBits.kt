package org.yunghegel.salient.modules.graphics.shared.config

enum class ColorBufferBits(r: Int, g: Int, b: Int, a: Int) {

    RGBA4444(4, 4, 4, 4),
    RGB565(5, 6, 5, 0),
    RGBA8888(8, 8, 8, 8),
    RGB888(8, 8, 8, 0),
    RGBA1010102(10, 10, 10, 2),
    RGB101111(10, 11, 11, 0),
    RGBA16161616(16, 16, 16, 16),
    RGBA32323232(32, 32, 32, 32);

    val r = r
    val g = g
    val b = b
    val a = a

    fun toInt(): Int {
        return r + g + b + a
    }

    companion object {

        fun fromInt(bits: Int): ColorBufferBits {
            return when (bits) {
                4    -> RGBA4444
                5    -> RGB565
                8    -> RGBA8888
                10   -> RGBA1010102
                16   -> RGBA16161616
                32   -> RGBA32323232
                else -> throw IllegalArgumentException("Invalid color buffer bits: $bits")
            }
        }
    }

}
