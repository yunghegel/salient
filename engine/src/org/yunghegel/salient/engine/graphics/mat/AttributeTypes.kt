package org.yunghegel.salient.engine.graphics.mat

object AttrKind {



    enum class Texture {
        Diffuse,
        Normal,
        Specular,
        Emissive,
        Ambient;

        companion object {
            fun fromString(str: String): Texture {
                return when(str) {
                    "diffuse" -> Diffuse
                    "normal" -> Normal
                    "specular" -> Specular
                    "emissive" -> Emissive
                    "ambient" -> Ambient
                    else -> return AttrKind.Texture.valueOf(str)
                }
            }
        }

    }

    enum class Float {
        Shininess,
        Alpha
    }


}
