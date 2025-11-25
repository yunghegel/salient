package org.yunghegel.salient.engine.input

import com.badlogic.gdx.InputProcessor

interface InputControls {

    fun pause()
    fun resume()

    fun resumeExcept(vararg processor: InputProcessor)

    fun pauseExcept(vararg processor: InputProcessor)
}