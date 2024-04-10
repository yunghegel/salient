package org.yunghegel.salient.engine.helpers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import org.yunghegel.salient.engine.graphics.DebugDrawer
import org.yunghegel.salient.engine.io.inject
import org.yunghegel.salient.engine.ui.UI


object TextRenderer {

    var font: BitmapFont

    val fontLarge: BitmapFont
    val fontSmall: BitmapFont
    val fontMedium: BitmapFont

    val fontBatch: SpriteBatch
    val camera: PerspectiveCamera

    val small = 0
    val medium = 1
    val large = 2

    val debugDrawer: DebugDrawer

    init {
        fontLarge = UI.skin.getFont("default-large")
        fontMedium = UI.skin.getFont("default-medium")
        fontSmall = UI.skin.getFont("default-small")

        debugDrawer = DebugDrawer()
        font = fontSmall
        fontBatch = SpriteBatch()
        camera = inject()
    }

    fun renderText(text: String, x: Float, y: Float, size: Int = 1) {

        val font = switchFont(size)
        fontBatch.begin()
        font.draw(fontBatch, text, x, y)
        fontBatch.end()
    }

    fun renderText(text: String, pos: Vector3, size: Int = 1) {

        val loc = camera.project(pos)
        fontBatch.projectionMatrix.setToOrtho2D(0f, 0f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        renderText(text, loc.x, loc.y, size)
    }

    fun <T:Any> T.drawText(text: String, x: Float, y: Float, size: Int = 1) {
        val font = switchFont(size)
        fontBatch.begin()
        font.draw(fontBatch, text, x, y)
        fontBatch.end()
    }

    fun <T:Any> T.drawText(text: String, worldPos: Vector3, size: Int = 1) {
        val projCoords = camera.project(worldPos)

        fontBatch.begin()
        font.draw(fontBatch, text, projCoords.x, projCoords.y)
        fontBatch.end()

    }

    fun switchFont(size: Int): BitmapFont {
        return when (size) {
            small  -> fontSmall
            medium -> fontMedium
            large  -> fontLarge
            else   -> font
        }
    }

    fun keyVal(pos: Vector2, key: String, value: String) {
        debugDrawer.keyValue(pos, key, value, font)
    }

    fun keyVal(x: Float, y: Float, key: String, value: String) {
        debugDrawer.keyValue(Vector2(x, y), key, value, font)
    }

}
