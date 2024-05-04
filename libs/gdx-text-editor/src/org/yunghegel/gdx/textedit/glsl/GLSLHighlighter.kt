package org.yunghegel.gdx.textedit.glsl


import com.badlogic.gdx.Input.Keys.G
import com.badlogic.gdx.graphics.Color
import com.kotcrab.vis.ui.util.highlight.BaseHighlighter
import org.yunghegel.gdx.textedit.builtin
import org.yunghegel.gdx.textedit.directive
import org.yunghegel.gdx.textedit.hexcode
import org.yunghegel.gdx.textedit.*
import org.yunghegel.gdx.textedit.parser.KeywordList
import org.yunghegel.gdx.textedit.util.regex
import org.yunghegel.gdx.textedit.util.word


class GLSLHighlighter : BaseHighlighter() {


    init {
        createRuleset(GLSL_DIRECTIVES, hexcode(directive))
        createRuleset(GLSL_KEYWORDS, hexcode(keyword))
        createRuleset(GLSL_BUILTINS, hexcode(builtin))
        createRuleset(GLSL_TYPES, hexcode(type))
        createRuleset(GLSL_FUNCTIONS, hexcode(number))


//        comment regex
        createRule("//.*", hexcode(comment))
        createRule("/\\*.*\\*/", hexcode(comment))
//        number regex
        createRule("\\b[0-9]+\\.[0-9]+\\b", hexcode(number))

//        string regex
//        createRule("\".*\"",colors.string)
//        createRule("'.'",colors.string)
//        operators
//        createRule("[\\+\\-\\*\\/]",colors.builtin)
//        createRule("[\\(\\)\\{\\}\\[\\]]",colors.builtin)
//        createRule("[\\=\\<\\>\\!\\&\\|\\?\\:]+",colors.builtin)

//

    }

    fun createRuleset(keywordList: KeywordList,color : Color) {
        keywordList.keywords.forEach { keyword ->
            val wordRegex = "\\b${keyword}\\b"
            createRule(wordRegex,color)
        }
    }

    fun createRule(regexString: String,color: Color) {
        addRule(regex(regexString,color))
    }




}