package org.yunghegel.salient.editor.render.lib

import org.yunghegel.salient.engine.ui.UI.priority

class RenderFunction( val priority: Int,val shouldRender: ()->Boolean = { true }, private val renderFunc: (Float) -> Unit) : Comparable<RenderFunction> {



    override fun compareTo(other: RenderFunction): Int {
        return priority.compareTo(other.priority)
    }

    fun render(delta: Float) = if (shouldRender()) renderFunc(delta) else Unit
}