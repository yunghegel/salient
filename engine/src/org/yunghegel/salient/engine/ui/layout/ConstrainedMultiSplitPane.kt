package org.yunghegel.salient.engine.ui.layout

import com.badlogic.gdx.scenes.scene2d.Actor
import com.kotcrab.vis.ui.widget.MultiSplitPane
import org.yunghegel.gdx.utils.data.Range
import org.yunghegel.gdx.utils.ext.*
import kotlin.math.max
import kotlin.math.min

open class ConstrainedMultiSplitPane(val vertical:Boolean) : MultiSplitPane(vertical) {

    enum class Adjustment {
        START,MIDDLE, END
    }

    val adj  = { index: Int ->
        when (index) {
            0 -> Adjustment.START
            1 -> Adjustment.END
            else -> Adjustment.MIDDLE
        }
    }

    val ranges = mutableMapOf<Actor,Range>()

    val cache = mutableMapOf<Actor, Float>()

    val prefs = mutableMapOf<Actor, Float>()

    val rangeResolution = mutableMapOf<Actor, (Actor)->Range>()

    val constraintCalculations = mutableMapOf<Actor, (Actor)->Float>()

    override fun setWidgets(vararg actors: Actor) {
        super.setWidgets(*actors)
        actors.forEach {
            ranges[it] = Range.of(0f,1f)
            cache[it] = 0f
            prefs[it] = 0f
        }
    }

    fun rangeResolutionFor(actor: Actor, resolution: (Actor)->Range) {
        rangeResolution[actor] = resolution
    }


    override fun setSplit(handleBarIndex: Int, split: Float) {
        val adjustment = adj(handleBarIndex)
        val actor = when (adjustment) {
            Adjustment.START -> children.first()
            Adjustment.END -> children.last()
            Adjustment.MIDDLE -> children[handleBarIndex]
        }
        val proposed = split
        val constrained = constrainedOrValid(actor,proposed,adjustment)
        if (!constrained.isNaN() or constrained.isInfinite()) {
            cache[actor] = constrained
            super.setSplit(handleBarIndex, constrained)
            return
        } else {
            super.setSplit(handleBarIndex, cache[actor] ?: prefs[actor] ?: 0f)
        }
        println(cache[actor])
    }

    fun setSplitInternal(handleBarIndex: Int, split: Float) {
        super.setSplit(handleBarIndex, split)
    }

    fun constrainedOrValid(actor: Actor, proposed: Float,adjustment: Adjustment) : Float {
        val constraint = prefs[actor] ?: -1f
        println("constraint: $constraint")
        if (constraint == -1f) return proposed
        val range = ranges[actor] ?: return proposed
        val min = range.start
        val max = range.end

        return when(adjustment) {
            Adjustment.START -> if (!vertical) max(proposed,constraint) else min(proposed,constraint)
            else -> if (!vertical) min(proposed,constraint) else max(proposed,constraint)
        }
    }

    override fun layout() {
        super.layout()
        children.each { actor ->
            rangeResolution[actor]?.let { resolution ->
                val range  = resolution(actor)
                ranges[actor] = range
            }
        }
    }

    fun restore(align: Int,actor:Actor) {

        when(align) {
            LEFT -> {
                val split = cache[actor] ?: prefs[actor] ?: 0f
                setSplit(0, split)
            }
            RIGHT -> {
                val cached = cache[actor] ?: prefs[actor] ?: 0f
                setSplit(1, cached)
            }
            BOTTOM -> {
                val cached = cache[actor] ?: prefs[actor] ?: 0.8f
                setSplit(0, cached)
            }
        }

    }

    fun hide(align: Int) {
        when(align) {
            LEFT -> {
                super.setSplit(0, 0f)
            }
            RIGHT -> {
                super.setSplit(1, 1f)
            }
            BOTTOM -> {
                super.setSplit(0, 1f)
            }
        }

    }

}
