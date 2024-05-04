package org.yunghegel.gdx.textedit.glsl

import org.yunghegel.gdx.textedit.parser.KeywordList


val GLSL_TYPES: KeywordList = KeywordList(
    "bool", "int", "double",
    "bvec2", "bvec3", "bvec4",
    "ivec2", "ivec3", "ivec4",
    "float", "vec2", "vec3", "vec4",
    "sampler2D", "samplerCube",
    "mat2", "mat3", "mat4"
)