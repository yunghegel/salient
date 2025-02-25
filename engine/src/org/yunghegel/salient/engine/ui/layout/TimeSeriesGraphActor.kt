package org.yunghegel.salient.engine.ui.layout

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Array as GdxArray
import com.badlogic.gdx.utils.TimeUtils
import org.yunghegel.salient.engine.system.InjectionContext.inject
import org.yunghegel.salient.engine.ui.scene2d.STable
import space.earlygrey.shapedrawer.GraphDrawer
import space.earlygrey.shapedrawer.JoinType
import space.earlygrey.shapedrawer.ShapeDrawer

class TimeSeriesGraphActor(
    private val shapeDrawer: ShapeDrawer = inject(),
    private val timeWindow: Float = 60f, // How many seconds of data to show
    private var updateInterval: Float = 0.1f, // How often to sample data (in seconds)
    private var joinType: JoinType = JoinType.SMOOTH,
    private var samples: Int = 50,
    private var rescale: Boolean = true
) : STable() {

    private val graphDrawer = GraphDrawer(shapeDrawer)
    private val graphBounds = Rectangle()

    // Data structures to hold the time series data
    private data class TimePoint(val time: Float, val value: Float)

    private val timePoints = GdxArray<TimePoint>()

    private var currentTime = 0f
    private var timeSinceLastUpdate = 0f
    private var valueProvider: () -> Float = { 0f }

    inner class ObserveredInput(var max: Float, var min: Float, var average: Float, var numPoints: Int) {

        var count = 0

        fun update(value: Float) {
            max = max.coerceAtLeast(value)
            min = min.coerceAtMost(value)
            average = (average * count + value) / (count + 1)
            count++
        }

        fun normalizeToDomain(domainFloor: Float, domainCeiling: Float, observedValue: Float): Float {
            return (observedValue - min) / (max - min) * (domainCeiling - domainFloor) + domainFloor
        }


    }

    val observeredInput = ObserveredInput(0f, 0f, 0f, samples)

    // Custom interpolation that uses the time series data
    private val timeSeriesInterpolation = object : Interpolation() {
        override fun apply(a: Float): Float {
            if (timePoints.isEmpty) return 0f

            // Convert input alpha (0-1) to time value
            val targetTime = currentTime - (1f - a) * timeWindow

            // Find the appropriate segment
            var i = timePoints.size - 1
            while (i > 0 && timePoints[i].time > targetTime) {
                i--
            }

            // If we're before the first point or after the last point
            if (i >= timePoints.size - 1) return timePoints.last().value
            if (i < 0) return timePoints.first().value

            // Linear interpolation between the two nearest points
            val p1 = timePoints[i]
            val p2 = timePoints[i + 1]
            val t = (targetTime - p1.time) / (p2.time - p1.time)
            var result = p1.value + (p2.value - p1.value) * t
            observeredInput.update(result)
            return observeredInput.normalizeToDomain(graphDrawer.domainBegin, graphDrawer.domainEnd, result)
        }
    }

    init {
        graphDrawer.apply {
            this.joinType = this@TimeSeriesGraphActor.joinType
            this.samples = this@TimeSeriesGraphActor.samples
            this.plotBegin = 0f
            this.plotEnd = 1f
            this.domainBegin = 0f
            this.domainEnd = 1f
            this.isRescale = this@TimeSeriesGraphActor.rescale
        }
    }

    override fun act(delta: Float) {
        super.act(delta)

        currentTime += delta
        timeSinceLastUpdate += delta

        // Update data points at the specified interval
        if (timeSinceLastUpdate >= updateInterval) {
            addTimePoint(valueProvider())
            timeSinceLastUpdate = 0f

            // Remove old points that are outside the time window
            val cutoffTime = currentTime - timeWindow
            while (timePoints.size > 0 && timePoints.first().time < cutoffTime) {
                timePoints.removeIndex(0)
            }
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        if (timePoints.isEmpty) return
        batch.end()
        graphBounds.set(x, y, width, height)

        graphDrawer.shapeDrawer.batch.begin()
        graphDrawer.draw(timeSeriesInterpolation, graphBounds)
        graphDrawer.shapeDrawer.batch.end()
        batch.begin()
    }

    /**
     * Sets the value provider function that will be sampled over time.
     * This function should return the current value to be graphed.
     */
    fun setValueProvider(provider: () -> Float) {
        valueProvider = provider
    }

    /**
     * Adds a time point to the graph.
     * @param value The value at the current time
     */
    private fun addTimePoint(value: Float) {
        timePoints.add(TimePoint(currentTime, value))
    }

    /**
     * Clears all time points from the graph.
     */
    fun clearData() {
        timePoints.clear()
        currentTime = 0f
        timeSinceLastUpdate = 0f
    }

    /**
     * Sets how many seconds of data to show on the graph.
     */
    fun setTimeWindow(seconds: Float) {
        require(seconds > 0f) { "Time window must be positive" }
        // Store current provider
        val currentProvider = valueProvider
        // Clear and reinitialize
        clearData()
        valueProvider = currentProvider
    }

    /**
     * Sets how frequently to sample new data points (in seconds).
     */
    fun setUpdateInterval(interval: Float) {
        require(interval > 0f) { "Update interval must be positive" }
        timeSinceLastUpdate = 0f
        updateInterval = interval
    }

    fun setJoinType(newJoinType: JoinType) {
        joinType = newJoinType
        graphDrawer.joinType = newJoinType
    }

    fun setSamples(newSamples: Int) {
        require(newSamples > 2) { "Number of samples must be greater than 2" }
        samples = newSamples
        graphDrawer.samples = newSamples
    }

    fun setRescale(shouldRescale: Boolean) {
        rescale = shouldRescale
        graphDrawer.isRescale = shouldRescale
    }

    /**
     * Gets all current time points in the graph.
     */
    fun getTimePoints(): List<Pair<Float, Float>> {
        return timePoints.map { Pair(it.time, it.value) }
    }
}