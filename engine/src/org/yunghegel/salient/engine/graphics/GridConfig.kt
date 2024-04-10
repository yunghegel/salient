package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.graphics.Color
import org.yunghegel.gdx.utils.reflection.Editable

class GridConfig(
    @Editable("Pixels Between Cells")
    var minPixelsBetweenCells: Float = 10.0f,
    @Editable("Cell Size")
    var gridCellSize: Float = 0.025f,
    @Editable("Color Thin")
    var gridColorThin: Color = Color(0.2f, 0.2f, 0.2f, 0.66f),
    @Editable("Color Thick")
    var gridColorThick: Color = Color(0.25f, 0.25f, 0.25f, 1f),
    @Editable("Total Size")
    var gridSize: Float = 50f,
    @Editable("Snap Enabled")
    var snap: Boolean = false,
    @Editable("Grid Enabled")
    var enabled: Boolean = true

) {

    fun set(
        gridSize: Float,
        gridCellSize: Float,
        minPixelsBetweenCells: Float,
        gridColorThin: Color,
        gridColorThick: Color
    ) {
        this.gridSize = gridSize
        this.gridCellSize = gridCellSize
        this.minPixelsBetweenCells = minPixelsBetweenCells
    }

    fun setColors(thin: Color, thick: Color) {
        gridColorThin = thin
        gridColorThick = thick
    }

    fun setSize(gridSize: Float, gridCellSize: Float, minPixelsBetweenCells: Float) {
        this.gridSize = gridSize
        this.gridCellSize = gridCellSize
        this.minPixelsBetweenCells = minPixelsBetweenCells
    }
}