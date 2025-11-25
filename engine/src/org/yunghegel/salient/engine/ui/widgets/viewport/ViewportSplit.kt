package org.yunghegel.salient.engine.ui.widgets.viewport

import com.badlogic.gdx.scenes.scene2d.Actor
import ktx.actors.onChange
import ktx.scene2d.textTooltip
import org.yunghegel.gdx.utils.data.Range
import org.yunghegel.gdx.utils.ext.MathUtils
import org.yunghegel.salient.engine.ui.layout.ConstrainedMultiSplitPane
import org.yunghegel.salient.engine.ui.layout.TabPanel
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.table
import kotlin.math.max

class ViewportSplit(val viewportPanel : ViewportPanel) : ConstrainedMultiSplitPane(false) {

    val currentActors : MutableList<Actor> = mutableListOf(viewportPanel)
    var guiSettings = table()
    init {
        setWidgets(viewportPanel)
        prefs[viewportPanel] = 0.75f
        val swapOrientation : SImageButton = SImageButton("ui_swap")
        val replace : SImageButton = SImageButton("ui_replace")

        swapOrientation.onChange {
            setVertical(!vertical)
        }
        swapOrientation.textTooltip("Set vertical/horizontal orientation")

        replace.onChange {
//            if we have at least 2 actors, swap their order
            if (currentActors.size>1) {
                val first = currentActors.first()
                val last = currentActors.last()
                currentActors[0] = last
                currentActors[currentActors.size-1] = first

                if (cache[first]!=null && cache[last]!=null) {
                    val tmp = cache[first]!!
                    cache[first] = cache[last]!!
                    cache[last] = tmp
                }

                setWidgets(*currentActors.toTypedArray())
                rebuildCaches()
            }
        }

        replace.textTooltip("Reverse panel ordering")

        guiSettings.add(swapOrientation).pad(2f)
        guiSettings.add(replace).pad(2f)


    }



    override fun setSplit(handleBarIndex: Int, split: Float) {
        val actor : Actor = when (handleBarIndex) {
            0 -> viewportPanel
            else -> currentActors[handleBarIndex]
        }
        val proposed = max(0.4f, split)
        var constrained = constrainedOrValid(actor,proposed,adj(handleBarIndex))
        if (!constrained.isNaN() or constrained.isInfinite()) {
            cache[viewportPanel] = constrained

            constrained = MathUtils.clamp(constrained,0.5f,0.8f)

            setSplitInternal(handleBarIndex,constrained)
            return
        } else {
            cache[actor] = proposed
            setSplitInternal(handleBarIndex,proposed)
        }

    }

    override fun setWidgets(vararg actors: Actor) {
        currentActors.clear()
        currentActors.addAll(actors)
        currentActors.forEach {  actor ->
            cacheRange(actor)
        }
        super.setWidgets(*actors)
    }

    fun append(actor: Actor) {
        currentActors.add(actor)
        super.setWidgets(*currentActors.toTypedArray())
        rebuildCaches()
        if (cache[viewportPanel]!=null) {
            setSplit(0,cache[viewportPanel]!!)
        }

    }

    fun removeLast() {
        currentActors.removeLast()
        super.setWidgets(*currentActors.toTypedArray())
        rebuildCaches()
    }

    fun remove(actor: Actor) {
        currentActors.remove(actor)
        super.setWidgets(*currentActors.toTypedArray())
        rebuildCaches()
    }

    fun rebuildCaches() {
        currentActors.forEach { actor ->
            cacheRange(actor)
            invalidateHierarchy()
            layout()
        }
        applyPrefs()
    }

    fun applyPrefs() {
        if (currentActors.size>1) {
            setSplit(0,prefs[currentActors[0]]!!)
        }
    }

    fun cacheRange(actor: Actor) {
        ranges[actor] = Range.Companion.of(0f,1f)
        rangeResolution[actor] = {
            val index = currentActors.indexOf(it)
            var start = 0f
            var end = 1f

            if (index>0) {
                start = prefs[currentActors[index-1]]!!
            } else {
                start = this@ViewportSplit.x
            }

            if (index<currentActors.size-1) {
                end = prefs[currentActors[index+1]]!!
            } else {
                end = this@ViewportSplit.y
            }

            Range.Companion.of(start,end).toNormalized(width)
        }
        if(prefs[actor]==null) prefs[actor] = when (children.size) {
            0 -> 1f
            else -> {
                val index = currentActors.indexOf(actor)
                val start = if (index>0) prefs[currentActors[index-1]]!! else 0f
                val end = if (index<currentActors.size-1) prefs[currentActors[index+1]]!! else 1f
                (start+end)/2
            }
        }
        cache[actor] = prefs[actor]!!
    }

    fun computeRange(actor: Actor): Range {
        return rangeResolution[actor]?.invoke(actor) ?: ranges[actor]!!
    }


}