package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion

val appwidth: Int
    get() = Gdx.graphics.width

val appheight: Int
    get() = Gdx.graphics.height

val appwidthf: Float
    get() = appwidth.toFloat()

val appheightf: Float
    get() = appheight.toFloat()


val delta : Float
    get() = Gdx.graphics.deltaTime

fun glEnable(glenum: Int) = Gdx.gl.glEnable(glenum)

fun glDisable(glenum: Int) = Gdx.gl.glDisable(glenum)

fun glBlendMode(src:Int,dst:Int) = Gdx.gl.glBlendFunc(src,dst)

fun createColorPixelTexture(color:Color): TextureRegion {
    return TextureRegion(Texture(createColoredPixelPixmap(color)))
}

fun createColorPixel(r: Float ,g: Float,b: Float, a: Float): TextureRegion {
    return createColorPixelTexture(Color(r,g,b,a))
}

private fun createColoredPixelPixmap(color: Color): Pixmap {
    val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
    pixmap.setColor(color)
    pixmap.fill()
    return pixmap
}