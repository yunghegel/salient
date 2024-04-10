package org.yunghegel.salient.editor.input

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.utils.SnapshotArray

typealias GdxInput = com.badlogic.gdx.Input


object Input : InputMultiplexer(),GdxInput by Gdx.input {

    init {
        inputProcessor = this
    }

    private var paused = false

    var tmp : SnapshotArray<InputProcessor> = SnapshotArray()

    object Buttons {
        const val LEFT = 0
        const val RIGHT = 1
        const val MIDDLE = 2
    }

    object Keys {
        const val ESCAPE = 131
        const val CONTROL_LEFT = 129
        const val CONTROL_RIGHT = 130
        const val SHIFT_LEFT = 59
        const val SHIFT_RIGHT = 60
    }

    fun pause(processor: InputProcessor) {
        paused = true

        tmp.clear()
        tmp.add(processor)
        tmp.addAll(processors)

        processors.clear()
        processors.addAll(tmp)
    }

    fun resume(processor: InputProcessor) {
        paused = false

        tmp.clear()
        tmp.addAll(processors)
        tmp.removeValue(processor,true)

        processors.clear()
        processors.addAll(tmp)
    }



}
