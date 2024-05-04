package org.yunghegel.salient.engine.ui.layout

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.IntFloatMap
import com.badlogic.gdx.utils.IntMap
import com.kotcrab.vis.ui.widget.MultiSplitPane
import com.kotcrab.vis.ui.widget.VisSplitPane
import org.yunghegel.gdx.utils.data.Range
import org.yunghegel.gdx.utils.data.RangeGroup
import org.yunghegel.gdx.utils.ext.*
import org.yunghegel.salient.engine.events.lifecycle.onEditorInitialized
import kotlin.math.max
import kotlin.math.min

open class MultiSplitPaneEx (vertical: Boolean): MultiSplitPane(vertical) {

    val prefs : IntFloatMap = IntFloatMap()
    val cache : IntFloatMap = IntFloatMap()
    var bounds : IntMap<Rectangle> = IntMap()

    private val getters : MutableMap<Int,()->Float> = mutableMapOf()

    var ranges = listOf<Range>()

    val rangeGroup : RangeGroup
        get() = RangeGroup(ranges)

    override fun setSplit(handleBarIndex: Int, split: Float) {
        println(rangeGroup.resolveRestricted(if(handleBarIndex==0) 0 else 1, split))
        val restriction = computeRestriction(handleBarIndex, split)
        if (restriction.isInfinite() || restriction.isNaN()){
            cache.put(handleBarIndex, split)
            super.setSplit(handleBarIndex, split)
        } else {
            cache.put(handleBarIndex, restriction)
            super.setSplit(handleBarIndex, restriction)
        }
    }

    fun setMinimum(range: Int, minimum: Float) {
       rangeGroup.setMinimum(range, minimum)
    }

    fun rangePercents() : List<Range> {
        val total = ranges.sum { it.length }
        val normalizedRanges  = mutableListOf<Range>()
        var start = 0f
        for (i in 0 until ranges.size - 1) {
            val range = ranges[i]
            val percent = range.length / total
            normalizedRanges.add(Range.of(start, start + percent,1/children.size.toFloat()))
            start += percent
        }
        return ranges
    }

    var initialized = false

    init {
        onEditorInitialized {
            layout()
            initialized = true
        }
    }

    override fun layout() {
        super.layout()
        println(rangePercents().map { it.format }.joinToString { "," })
    }

    fun setSplitInternal(handleBarIndex: Int, split: Float) {
        super.setSplit(handleBarIndex, split)
    }

    fun restoreSplit(handleBarIndex: Int) {
        super.setSplit(handleBarIndex, cache.get(handleBarIndex, 0f))
    }

    fun hide(handleBarIndex: Int,orientation: Int) {
        when (orientation) {
            LEFT -> {
                bounds.get(handleBarIndex).apply { children[handleBarIndex].getBounds(this) }
                super.setSplit(handleBarIndex, 0f)
            }
            RIGHT -> {
                bounds.get(handleBarIndex).apply { children[handleBarIndex].getBounds(this) }
                super.setSplit(handleBarIndex, 1f)
            }
        }
    }

    override fun setWidgets(vararg actors: Actor) {
        actors.forEachIndexed { index, actor ->

        }
        super.setWidgets(*actors)
    }

    private fun registerMinimum(pref: Float, forIndex: Int) {
        prefs.put(forIndex, pref)
        bounds.put(forIndex, Rectangle())
    }

    fun registerComputed(forIndex: Int, getter: ()->Float) {
        getters.put(forIndex, getter)
        super.setSplit(forIndex, getter.invoke())
        registerMinimum(getter.invoke(), forIndex)
    }

    private fun computeRestriction(index: Int, proposed:Float) : Float {
        if (getters.containsKey(index)) prefs.put(index, getters.get(index)!!.invoke())
        val pref = prefs.get(index,-1f)
        return when (index ==0 ) {
            true -> max(pref, proposed)
            false -> min(pref, proposed)
        }

    }

    fun widgetBounds(index: Int) : Rectangle {
        return bounds.get(index).apply { children[index].getBounds(this) }
    }

}

class SplitPaneEx(first:Actor,second:Actor,vertical: Boolean,val reverse:Boolean = false) : VisSplitPane(first,second,vertical) {

    var restrictionComputation : () -> Float = {0f}
    var cached : Float = 0f

    override fun setSplitAmount(amount: Float) {
        if (amount == 0f ) {
            super.setSplitAmount(cached)
        } else {
            cached = evaluateRestriction(amount)
            super.setSplitAmount(cached)
        }

        println("cached: $cached, current: $split, amount: $amount")

    }

    fun setSplitInternal(amount:Float) {
        super.setSplitAmount(amount)
    }

    fun setComputation(computation: () -> Float) {
        restrictionComputation = computation

    }

    fun evaluateRestriction(proposed: Float) : Float {
        val restriction = restrictionComputation.invoke()


        return when (proposed > restriction) {

            true -> restriction
            false -> proposed
        }
    }

    fun hide(orientation: Int) {
        when (orientation) {
            LEFT -> setSplitInternal(0f)
            RIGHT -> super.setSplitAmount(1f)
            TOP -> super.setSplitAmount(0f)
            BOTTOM -> setSplitInternal(1f)
        }
    }

    fun restoreSplit() {

        super.setSplitAmount(cached)
    }

}