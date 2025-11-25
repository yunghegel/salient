package org.yunghegel.salient.editor.app

import com.badlogic.gdx.Game
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.yunghegel.gdx.utils.ext.watch
import org.yunghegel.salient.engine.Phase
import org.yunghegel.salient.engine.Startup

class SplashScreen(main : Game, val startupTask: () -> Context) : ScreenAdapter() {

    val font : BitmapFont = BitmapFont()
    val stage : Stage = Stage(ScreenViewport())
    val batch : SpriteBatch = SpriteBatch()

    var text = "Loading..."
    var percent = 0f

    init {
        watch(Startup::state) { newState ->
            println("SplashScreen detected state change: $newState")
            text = "Loading... $newState"
            percent = (newState.ordinal.toFloat() / Phase.entries.size.toFloat()) * 100f
        }

        Startup.state
    }


}