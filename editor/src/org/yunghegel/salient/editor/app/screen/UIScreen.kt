package org.yunghegel.salient.editor.app.screen

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.ScreenViewport
import org.yunghegel.gdx.utils.ext.Platform

abstract class UIScreen : BaseScreen() {

    val stage : Stage = Stage(ScreenViewport(),Platform.createSpriteBatch())

    override fun render(delta: Float) {
        newFrame()
        renderUI(delta)
    }

    fun renderUI(delta: Float) {
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        stage.viewport.update(width, height, true)
    }

}