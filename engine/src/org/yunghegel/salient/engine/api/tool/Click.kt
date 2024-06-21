package org.yunghegel.salient.engine.api.tool

import com.badlogic.gdx.Gdx

data object Click {
    var x: Int = 0
    var y: Int = 0
    var button: Int = 0
    var time: Long = 0
    var tapcount: Int = 1
    var doubleClick: Boolean = false

    fun set(x: Int, y: Int, button: Int, time: Long, tapcount: Int, doubleClick: Boolean) {
        this.x = x
        this.y = y
        this.button = button
        this.time = time
        this.tapcount = tapcount
        this.doubleClick = doubleClick
    }

    fun update() {
        x = Gdx.input.x
        y = Gdx.input.y

    }

}