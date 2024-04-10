package org.yunghegel.salient.editor.render

import com.badlogic.ashley.core.Component

class RenderFunctionComponent(val prio: Int) : Component, Comparable<RenderFunctionComponent> {

    var renderFunction: (Float) -> Unit = {}

    constructor(renderFunction: (Float) -> Unit, prio : Int = 0) : this(prio) {
        this.renderFunction = renderFunction
    }

    fun render(delta: Float) {
        renderFunction(delta)
    }

    override fun compareTo(other: RenderFunctionComponent): Int {
        return prio.compareTo(other.prio)
    }
}