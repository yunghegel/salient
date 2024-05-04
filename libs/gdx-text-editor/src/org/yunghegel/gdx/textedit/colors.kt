package org.yunghegel.gdx.textedit

import com.badlogic.gdx.graphics.Color

/**
 * Colors used by the editor encoded as hex ARGB
 */



val background = "#1d1d1d"

val gutter = "#353535"

val selection = "#2c2c2c"

val text = "#69b48e"

val comment = "#5a5852"

val builtin = "#4a9976"

val keyword = "#218d89"

val directive = "#7081b6"

val number = "#dc903f"

val type = "#b45a31"

val string = "#5ab977"

fun hexcode(hex: String): Color {
    val r = Integer.valueOf(hex.substring(1, 3), 16)
    val g = Integer.valueOf(hex.substring(3, 5), 16)
    val b = Integer.valueOf(hex.substring(5, 7), 16)
    return Color(r / 255f, g / 255f, b / 255f, 1f)
}

val main = "void main() {\n" +
        "\t#ifdef blendedTextureFlag\n" +
        "\t\tif (texture2D(u_diffuseTexture, v_texCoords0).a < u_alphaTest)\n" +
        "\t\t\tdiscard;\n" +
        "\t#endif // blendedTextureFlag\n" +
        "\n" +
        "\t#ifdef PackedDepthFlag\n" +
        "\t\tHIGH float depth = v_depth;\n" +
        "\t\tconst HIGH vec4 bias = vec4(1.0 / 255.0, 1.0 / 255.0, 1.0 / 255.0, 0.0);\n" +
        "\t\tHIGH vec4 color = vec4(depth, fract(depth * 255.0), fract(depth * 65025.0), fract(depth * 16581375.0));\n" +
        "\t\tgl_FragColor = color - (color.yzww * bias);\n" +
        "\t#endif //PackedDepthFlag\n" +
        "}"





