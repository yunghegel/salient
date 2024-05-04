package org.yunghegel.gdx.textedit.glsl

import org.yunghegel.gdx.textedit.parser.KeywordList


val GLSL_BUILTINS: KeywordList = KeywordList(
    "gl_Position", "gl_FragColor", "gl_FragCoord",
    "gl_PointSize", "gl_FrontFacing", "gl_PointCoord", "gl_PointSize",
    "GL_ES", "GL_OES_standard_derivatives"
)