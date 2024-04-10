package org.yunghegel.salient.engine.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Tooltip
import com.badlogic.gdx.scenes.scene2d.ui.TooltipManager
import com.badlogic.gdx.utils.Align
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.textTooltip
import ktx.scene2d.tooltip
import org.yunghegel.gdx.utils.ext.alpha
import org.yunghegel.salient.engine.io.Memory
import org.yunghegel.salient.engine.ui.addTooltip
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable

class PercentageIndicator(val prefix: String,val percentSource: ()->Float) : STable() {

    data class Range(val min: Float, val max: Float, val color: Color)

    var ranges : List<Range> = listOf()

   val label : Label
   val prefixLabel : Label

    init {
        ranges = listOf(
            Range(0f, 0.25f, Color.GREEN.cpy().alpha(0.5f)),
            Range(0.25f, 0.5f, Color.YELLOW.cpy().alpha(0.5f)),
            Range(0.5f, 0.75f, Color.ORANGE.cpy().alpha(0.5f)),
            Range(0.75f, 1f, Color.RED.cpy().alpha(0.5f))
        )
        prefixLabel = SLabel("$prefix:")
        label = SLabel("$prefix: 0%")

        if (prefix.isNotBlank()) add(prefixLabel).padRight(6f)
        add(label)


    }

    fun formatPercentage(percent: Float,numDecimals :Int =2): String {
        return String.format("%.${numDecimals}f", percent)
    }

    override fun act(delta: Float) {
        val percent = percentSource.invoke()
        ranges.forEach { range ->
            if (percent/100 in range.min..range.max) {
                label.setColor(range.color)
                return@forEach
            }
        }
        label.setText("${formatPercentage(percent)}%")

        super.act(delta)
    }

}