package org.yunghegel.gdx.utils.data

import com.badlogic.gdx.utils.IntFloatMap
import ktx.collections.GdxArray
import org.yunghegel.gdx.utils.ext.MathUtils
import org.yunghegel.gdx.utils.ext.sum

class RangeGroup( val ranges : List<Range>) {

    val length: Float
        get() = ranges.sum { it.length }

    val percentSplits : List<Range>
        get() = ranges.map { it.toNormalized(length)}

    var cache : IntFloatMap = IntFloatMap()

    var minimums : IntFloatMap = IntFloatMap()

    enum class Position {
        START, END
    }

    init{
        for (i in 0 until ranges.size) {
            setMinimum(i, 0f)
        }
    }

    fun setMinimum(index: Int, value: Float){
        minimums.put(index, value)
    }

    fun adjust(position: Position,index: Int, value: Float){
        val range = ranges[index]
        when(position){
            Position.START -> {
                range.start = value
            }
            Position.END -> {
                range.end = value
            }
        }
        if (index < ranges.size - 1){
            val next = ranges[index + 1]
            next.start = range.end
        } else if (index > 0){
            val prev = ranges[index - 1]
            prev.end = range.start
        }
    }


    fun resolveRestricted(index: Int, value: Float) : Float{
        val range = ranges[index]
        val min = minimums.get(index, 0f)
        val max = minimums.get(index + 1, 0f)
        val split =MathUtils.clamp(value, min, max)
        cache.put(index, split)
        return split
    }

    fun resolveRestrictedNormalized(index:Int,percent: Float) : Float{
        val range = percentSplits[index]
        val percentMin = minimums.get(index, 0f)/length
        val percentMax = minimums.get(index + 1, 0f)/length
        val split = MathUtils.clamp(percent, percentMin, percentMax)
        cache.put(index, split)
        return split
    }



    fun layout(){
        val total = length
        var start = 0f
        for (i in 0 until ranges.size - 1) {
            val range = ranges[i]
            println(range.prefererred)
            val percent = range.length / total
            percentSplits[i].start = start
            percentSplits[i].end = start + percent
            start += percent
        }
    }

    fun setSplitPercentAndLayout(index: Int, percent: Float){
        val total = length
        val range = ranges[index]
        val percentRange = percentSplits[index]
        val percentLength = range.length * percent
        percentRange.start = range.start
        percentRange.end = range.start + percentLength
        range.start += percentLength
        layout()
    }

}