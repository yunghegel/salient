package org.yunghegel.salient.engine.api.undo

import org.yunghegel.salient.engine.system.inject

abstract class Action {

    open var userdata : Any = Any()

    open var name = "Unnamed"

    abstract fun undo()

    abstract fun exec()

    fun add() {
        val actionHistory : ActionHistory = inject()
        actionHistory.add(this)
    }

}
