package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.PolygonBatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g3d.utils.ShapeCache
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import org.yunghegel.salient.engine.graphics.shapes.utility.Grid
import org.yunghegel.salient.engine.graphics.util.DebugDrawer
import space.earlygrey.shapedrawer.ShapeDrawer

interface SharedGraphicsResources {

    val spriteBatch: SpriteBatch
    val debugDrawer: DebugDrawer
    val shapeDrawer: ShapeDrawer
    val grid : Grid
    val shapeRenderer: ShapeRenderer
    val whitePixel : TextureRegion
    val polygonBatch: PolygonBatch
    val font : BitmapFont
}