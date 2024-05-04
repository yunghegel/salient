package org.yunghegel.gdx.textedit.glsl

import org.yunghegel.gdx.textedit.parser.KeywordList


val GLSL_FUNCTIONS: KeywordList = KeywordList(
    "radians", "degrees", "sin", "cos", "tan", "asin", "acos", "atan",
    "pow", "exp", "log", "exp2", "log2", "sqrt", "inversesqrt", "abs", "sign",
    "floor", "ceil", "fract", "mod", "min", "max", "clamp", "mix", "step", "smoothstep",
    "lenght", "distance", "dot", "cross", "normalize", "faceforward", "reflect", "refract",
    "matrixCompMult", "lessThan", "lessThanEqual", "greaterThan", "greaterThanEqual", "equal", "notEqual", "any",
    "all", "not", "texture2DLod", "texture2DProjLod", "texture2DProjLod", "textureCubeLod",
    "texture2D", "texture2DProj", "texture2DProj", "textureCube"
)