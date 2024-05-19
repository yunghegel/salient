package org.yunghegel.salient.engine.ui.layout

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import org.yunghegel.gdx.utils.ext.getBounds
import org.yunghegel.gdx.utils.ext.ref
import org.yunghegel.gdx.utils.ext.sizeHashCode
import org.yunghegel.gdx.utils.memoize



/**
 * Describes the minimum width/height allowable for an item in a split pane, optionally
 * you can define a maximum that will be used to constrain the item's size
 * When between min and max, we size according to the percentage value as the application size changes
 */
class SplitPreference(private var _min: Float,private var _max: Float, private var _percentage: Float) {
    var min: Float by ref(_min)
    var max: Float by ref(_max)
    var percentage: Float  by ref(_percentage)
}


/**
 * An interface that allows you to define the minimum and maximum split values for an actor
 */
interface ActorSplitPreference {

    fun minSplit(): Float

    fun maxSplit(): Float = -1f

    fun usePercentage(): Float

    val preference : SplitPreference
        get() = SplitPreference(minSplit(),maxSplit(),usePercentage())

}

/**
 * An enum class that describes the order of an actor in a split pane
 * An actor at the start or end can only enforce a minimum size, an actor in the middle can enforce a minimum and maximum size
 * However, when both satisfy the constraints, the middle actors will be sized according to the percentage value
 */
enum class ActorOrder {
    START, MIDDLE, END
}

class SplitContext<T ,Parent>(val parent: Parent, val participants: List<T>) : WidgetGroup() where T : ActorSplitPreference, T : Actor, Parent : Actor{



    private val _parentBounds: (Int)->Rectangle = { _ : Int -> parent.getBounds() }.memoize(100)
    private val _appBounds: (Int, Int)->Rectangle = { _:Int, _:Int, -> Rectangle(0f,0f, Gdx.graphics.width.toFloat(),
        Gdx.graphics.height.toFloat()
    ) }.memoize(100)

    var vertical = true
    val parentBounds : Rectangle
        get() = _parentBounds(parent.sizeHashCode())

    val appBounds : Rectangle
        get() = _appBounds(Gdx.graphics.width, Gdx.graphics.height)

    val splitPrefs = mutableMapOf<Actor,()->Float>()

    fun addSplitPreference(actor: Actor, preference: ()->Float) {
        splitPrefs[actor] = preference
    }

    override fun layout() {
        super.layout()
    }



    val map : Map<Actor,ActorConfig>
        get() {
            val prefs = mutableMapOf<Actor,ActorConfig>()
            participants.forEach {
                val order = when {
                    it == participants.first() -> ActorOrder.START
                    it == participants.last() -> ActorOrder.END
                    else -> ActorOrder.MIDDLE
                }
                val split = splitPrefs[it]?.invoke() ?: 0f
                prefs[it] = ActorConfig(it,order,split,it.preference)
            }
            return prefs
        }

}


/**
 * Associates an actor with its preferences to be used in a split pane
 */
data class ActorConfig(val actor: Actor, val position:ActorOrder, val currentSplit: Float, val prefs: SplitPreference)

interface SplitConfigurator {

    fun acceptOrReject(context: SplitContext<*,*>,proposedSplit: Float, config: ActorConfig): Float

}

object DefaultSplitConfigurator : SplitConfigurator {

    override fun acceptOrReject(context: SplitContext<*,*>, proposedSplit: Float, config: ActorConfig): Float {
        val min = config.prefs.min
        val max = config.prefs.max
        val percentage = config.prefs.percentage
        val position = config.position
        val currentSplit = config.currentSplit
        val res =  when {
            proposedSplit < min -> min
            proposedSplit > max -> max
            position == ActorOrder.MIDDLE -> percentage
            else -> currentSplit
        }
        println("Proposed: $proposedSplit, Min: $min, Max: $max, Position: $position, Current: $currentSplit, Res: $res")
        return res
    }

}

