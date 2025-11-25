package org.yunghegel.salient.editor.render.systems

import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.editor.render.lib.RenderFunction

open class RenderingSystem() : BaseSystem("rendering_sy,9)stem",9) {

    private val renderFunctions = mutableListOf<RenderFunction>()
    private val renderFunctionsDebug = mutableListOf<RenderFunction>()

    var debug = false
    var debugOnly = false

    var lastRenderFunc = 0

    private var mode = Mode.STANDARD
        get() = if (debug) { if (debugOnly) Mode.DEBUG_ONLY else Mode.DEBUG
        } else Mode.STANDARD

    private enum class Mode {
        STANDARD,DEBUG,DEBUG_ONLY
    }

    fun addRenderFunction(prio:Int,debug:Boolean = false, shoulddRender: () -> Boolean = { true }, renderFunction: (Float) -> Unit) {
        if (debug) renderFunctionsDebug.add(RenderFunction(prio, shoulddRender, renderFunction, )).also {
            renderFunctionsDebug.sort()
        }
        else renderFunctions.add(RenderFunction(prio, shoulddRender, renderFunction,)).also {
            renderFunctions.sort()
        }
    }

    fun removeRenderFunction(prio:Int,debug:Boolean = false) {
        val renderFunctions = if (debug) renderFunctionsDebug else renderFunctions
        renderFunctions.removeIf { it.priority == prio }
        renderFunctions.sort()
    }

    override fun update(deltaTime: Float) {
        when (mode) {
            Mode.STANDARD -> render(deltaTime)
            Mode.DEBUG -> render(deltaTime).also{ renderDebug(deltaTime) }
            Mode.DEBUG_ONLY -> renderDebug(deltaTime)
        }
    }

    open fun render(deltaTime: Float) {
        renderFunctions.sortedByDescending { it.priority }.forEach { it.render(deltaTime) }
    }

    open fun renderDebug(deltaTime: Float) {
        renderFunctionsDebug.forEach { it.render(deltaTime) }
    }

}