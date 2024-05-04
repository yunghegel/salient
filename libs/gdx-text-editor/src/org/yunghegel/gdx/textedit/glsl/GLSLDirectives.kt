package org.yunghegel.gdx.textedit.glsl

import org.yunghegel.gdx.textedit.parser.KeywordList


val GLSL_DIRECTIVES: KeywordList = KeywordList(
    "#define", "precision", "#extension", "#ifdef", "#endif", "#else", "struct",
    "#undef", "#ifndef", "error", "pragma", "#version", "line", "if", "elif",
    "uniform", "const", "attribute", "varying", "highp", "mediump", "lowp", "defined","MED","HIGH","LOW"
)