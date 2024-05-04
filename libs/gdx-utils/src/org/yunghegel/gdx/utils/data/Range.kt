package org.yunghegel.gdx.utils.data

interface Range {

    var start : Float

    var end : Float

    var prefererred : Float

    val length : Float
        get() = end - start



    fun toNormalized(total : Float) : Range {
        val start = this.start / total
        val end = this.end / total
        return Range.of(start, end, prefererred / total)
    }

    val format : String
        get() = "[$start -> $end ($length)]"


    companion object {
        fun of(start : Float, end : Float, pref: Float=0.5f) : Range {
            return object : Range {
                override var start = start
                override var end = end
                override var prefererred = pref
            }
        }
    }


}