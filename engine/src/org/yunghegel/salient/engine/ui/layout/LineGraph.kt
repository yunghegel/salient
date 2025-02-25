package org.yunghegel.salient.engine.ui.layout

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import org.yunghegel.salient.engine.system.InjectionContext.inject
import org.yunghegel.salient.engine.ui.scene2d.STable
import space.earlygrey.shapedrawer.GraphDrawer
import space.earlygrey.shapedrawer.JoinType
import space.earlygrey.shapedrawer.ShapeDrawer

class LineGraph(
    private val shapeDrawer: ShapeDrawer = inject(),
    private var interpolation: Interpolation = Interpolation.linear,
    private var joinType: JoinType = JoinType.SMOOTH,
    private var samples: Int = 50,
    private var plotRange: ClosedFloatingPointRange<Float> = 0f..1f,
    private var domainRange: ClosedFloatingPointRange<Float> = 0f..1f,
    private var rescale: Boolean = true
) : STable() {

    private val graphDrawer = GraphDrawer(shapeDrawer)
    private val graphBounds = Rectangle()

    init {
        // Configure the GraphDrawer with initial settings
        graphDrawer.apply {
            this.joinType = this@LineGraph.joinType
            this.samples = this@LineGraph.samples
            this.plotBegin = plotRange.start
            this.plotEnd = plotRange.endInclusive
            this.domainBegin = domainRange.start
            this.domainEnd = domainRange.endInclusive

        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        // Update the graph bounds based on the actor's current position and size
        graphBounds.set(x, y, width, height)

        // Ensure the ShapeDrawer is using our batch


        // Draw the graph
        graphDrawer.draw(interpolation, graphBounds)
    }

    /**
     * Updates the interpolation function used for the graph.
     */
    fun setInterpolation(newInterpolation: Interpolation) {
        interpolation = newInterpolation
    }

    /**
     * Updates the join type used for connecting points.
     */
    fun setJoinType(newJoinType: JoinType) {
        joinType = newJoinType
        graphDrawer.joinType = newJoinType
    }

    /**
     * Updates the number of samples used to draw the graph.
     */
    fun setSamples(newSamples: Int) {
        require(newSamples > 2) { "Number of samples must be greater than 2" }
        samples = newSamples
        graphDrawer.samples = newSamples
    }

    /**
     * Updates the plot range (where the graph starts and ends on the x-axis).
     */
    fun setPlotRange(range: ClosedFloatingPointRange<Float>) {
        require(range.start < range.endInclusive) { "Plot begin must be less than plot end" }
        plotRange = range
        graphDrawer.plotBegin = range.start
        graphDrawer.plotEnd = range.endInclusive
    }

    /**
     * Updates the domain range (which portion of the plot is visible).
     */
    fun setDomainRange(range: ClosedFloatingPointRange<Float>) {
        require(range.start < range.endInclusive) { "Domain begin must be less than domain end" }
        domainRange = range
        graphDrawer.domainBegin = range.start
        graphDrawer.domainEnd = range.endInclusive
    }

    /**
     * Updates whether the graph should rescale to fit within its bounds.
     */
    fun setRescale(shouldRescale: Boolean) {
        rescale = shouldRescale
        graphDrawer.isRescale = shouldRescale
    }
}