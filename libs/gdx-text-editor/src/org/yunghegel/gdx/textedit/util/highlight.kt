package org.yunghegel.gdx.textedit.util

import com.badlogic.gdx.graphics.Color
import com.kotcrab.vis.ui.util.highlight.HighlightRule
import com.kotcrab.vis.ui.util.highlight.RegexHighlightRule
import com.kotcrab.vis.ui.util.highlight.WordHighlightRule

fun regex(regexString: String,color: Color) : HighlightRule {
    return RegexHighlightRule(color,regexString)
}

fun word(word: String,color: Color) : HighlightRule {
    return WordHighlightRule(color,word)
}