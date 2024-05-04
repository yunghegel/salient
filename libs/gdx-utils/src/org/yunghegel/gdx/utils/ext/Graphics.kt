package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.Gdx

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