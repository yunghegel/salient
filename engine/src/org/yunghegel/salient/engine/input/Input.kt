package org.yunghegel.salient.engine.input

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.utils.SnapshotArray
import ktx.inject.Context
import org.yunghegel.salient.engine.InputModule

typealias GdxInput = Input

object Input : InputModule() {

    init {
        inputProcessor = this
    }

    override val registry: Context.() -> Unit = {
        bindSingleton(InputMultiplexer::class, InputAdapter::class) { this }
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

    fun pauseExcept(processor: InputProcessor) {
        paused = true

        tmp.clear()
        tmp.add(processor)
        tmp.addAll(processors)

        processors.clear()
        processors.add(processor)
    }

    fun resumeExcept(processor: InputProcessor) {
        paused = false

        tmp.clear()
        tmp.addAll(processors)
        tmp.removeValue(processor,true)

        processors.clear()
        processors.addAll(tmp)
    }

    fun pause() {
        paused = true

        tmp.clear()
        tmp.addAll(processors)

        processors.clear()
    }

    fun resume() {
        paused = false

        processors.clear()
        processors.addAll(tmp)
    }

}