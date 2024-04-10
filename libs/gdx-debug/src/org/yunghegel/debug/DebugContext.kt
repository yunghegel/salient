package org.yunghegel.debug

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.utils.ShapeCache
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import org.yunghegel.gdx.utils.data.Mask

data class DebugContext(
    val shapeRenderer: ShapeRenderer,
    val camera: Camera,
    val font: BitmapFont,
    val spriteBatch:SpriteBatch,
    val modelBatch: ModelBatch,
    val shapeCache: ShapeCache
) {


    init {

    }

    fun start(mask: Int) {

        var is3D = mask and IS_3D != 0

        if (mask and USES_SHAPE_RENDERER != 0) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
            if (is3D) {
                shapeRenderer.projectionMatrix = camera.combined
            }
        }
        if (mask and USES_SPRITE_BATCH != 0) {
            spriteBatch.begin()
        }
        if (mask and USES_MODEL_BATCH != 0) {
            modelBatch.begin(camera)
        }


    }

    fun end(mask: Int) {
        if (mask and USES_SHAPE_RENDERER != 0) {
            shapeRenderer.end()
        }
        if (mask and USES_SPRITE_BATCH != 0) {
            spriteBatch.end()
        }
        if (mask and USES_MODEL_BATCH != 0) {
            modelBatch.end()
        }
    }

}
