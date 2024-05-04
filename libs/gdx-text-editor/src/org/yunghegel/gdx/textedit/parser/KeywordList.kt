package org.yunghegel.gdx.textedit.parser

import ktx.collections.GdxArray
import kotlin.math.max

class KeywordList(vararg keywords: String) {

    var keywords: GdxArray<String>
    var maxLength: Int = 0

    init {
        var len = 0
        for (kw in keywords) {
            len = max(len.toDouble(), kw.length.toDouble()).toInt()
        }

        this.keywords = GdxArray(keywords)
        this.maxLength = len
    }

    fun isKeyword(buf: CharArray, start: Int, len: Int): Boolean {
        if (len > maxLength) {
            return false
        }

        val kwidx = 0
        var text = ""
        for (chpos in 0 until len) {
            text += buf[start + chpos]
        }

        for (k in keywords) {
            if (k.equals(text, ignoreCase = true)) {
                return true
            }
        }

        return false
    }
}