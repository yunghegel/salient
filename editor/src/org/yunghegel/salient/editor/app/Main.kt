package org.yunghegel.salient.editor.app

import com.badlogic.gdx.Game

object Main : Game()   {
    override fun create() {
        setScreen(Salient())
    }
}