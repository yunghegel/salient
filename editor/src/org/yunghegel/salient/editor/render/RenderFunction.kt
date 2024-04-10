package org.yunghegel.salient.editor.plugins.render

class RenderFunction(private val renderFunc: (Float) -> Unit, val priority: Int) : Comparable<RenderFunction> {

    override fun compareTo(other: RenderFunction): Int {
        return priority.compareTo(other.priority)
    }

    fun render(delta: Float) = renderFunc(delta)
}