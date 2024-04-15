package org.yunghegel.salient.editor.app.configs.graphics

import com.badlogic.gdx.Gdx


enum class Resolution(val width: Int, val height: Int) {

    _640x480(640, 480),
    _800x600(800, 600),
    _1024x768(1024, 768),
    _1280x720(1280, 720),
    _1280x800(1280, 800),
    _1280x1024(1280, 1024),
    _1366x768(1366, 768),
    _1440x900(1440, 900),
    _1600x900(1600, 900),
    _1680x1050(1680, 1050),
    _1920x1080(1920, 1080),
    _1920x1200(1920, 1200),
    _2560x1440(2560, 1440),
    _2560x1600(2560, 1600),
    _3840x2160(3840, 2160);

    companion object {

        fun detectNativeRes(): Pair<Int, Int> {
            val x = Gdx.graphics.monitor.virtualX
            val y = Gdx.graphics.monitor.virtualY

            return Pair(x, y)
        }
    }

    override fun toString(): String {
        return name.substring(1)
    }

}
