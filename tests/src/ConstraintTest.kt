import com.badlogic.gdx.scenes.scene2d.Actor
import com.kotcrab.vis.ui.widget.MultiSplitPane
import org.yunghegel.salient.engine.ui.layout.*
import org.yunghegel.salient.engine.ui.scene2d.STable
import types.lwjgl3test

val ConstrainedTest = lwjgl3test("ConstraintSplitPane") {

    execCreate = {
        val table1 = ConstrainedTable(0f, 0.2f, 0.5f)
        val table2 = ConstrainedTable(0f, 1f, 0.5f)
        val table3 = ConstrainedTable(0.8f, 0.3f, 0.5f)
        val splitPane = ConstrainedSplitPane(DefaultSplitConfigurator, false, table1, table2, table3)
        stage.addActor(splitPane)
        splitPane.setFillParent(true)

    }

    execRender = {
        stage.act()
        stage.draw()
    }



}

class ConstrainedTable(val min: Float, val max: Float, val percent: Float) : STable(), ActorSplitPreference {

    override fun minSplit(): Float {
        return min
    }

    override fun usePercentage(): Float {
        return percent
    }

    override fun maxSplit(): Float {
        return max
    }

}

class ConstrainedSplitPane<T>(val splitConfigurator: SplitConfigurator,vertical: Boolean, vararg val splitActors: T) : MultiSplitPane(vertical) where T : ActorSplitPreference, T : Actor {

    var splitContext: SplitContext<*, Actor> = SplitContext(this, splitActors.toList())

    var splitPreferences = splitActors.map { it.preference }

    val actorConfigs = splitActors.map { it to splitConfigurator }.toMap()

    var cache = mutableMapOf<Actor, Float>()


    init {
        setWidgets(*splitActors)
    }

    fun resolveIndex (handleBarIndex: Int): Actor {
        return when (handleBarIndex) {
            0 -> children.first()
            children.size - 1 -> children.last()
            else -> children[handleBarIndex]
        }
    }

    override fun setSplit(handleBarIndex: Int, split: Float) {
        val actor = resolveIndex(handleBarIndex)
        val proposed = split
        val constrained = splitConfigurator.acceptOrReject(splitContext,split,splitContext.map[actor]!!)
        println("Constrained: $constrained")
        if (!constrained.isNaN() or constrained.isInfinite()) {
            cache[actor] = constrained
            super.setSplit(handleBarIndex, constrained)
            return
        }
    }






}