package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import org.yunghegel.salient.engine.graphics.shapes.utility.Grid
import org.yunghegel.salient.engine.graphics.util.DebugDrawer
import space.earlygrey.shapedrawer.ShapeDrawer

interface SharedGraphicsResources {

    var spriteBatch: SpriteBatch
    var debugDrawer: DebugDrawer
    var shapeDrawer: ShapeDrawer
    var grid : Grid
    var shapeRenderer: ShapeRenderer
    var whitePixel : TextureRegion

}