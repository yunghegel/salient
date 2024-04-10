package org.yunghegel.salient.engine.api.undo

import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.history.ActionExecutedEvent

class ActionBuilder(conf: ActionBuilder.() -> Unit) {

    var doAction = {}

    var undoAction = {}

    var name = "Unnamed"

    var data = Any()

    init {
        conf()
    }

    fun build(): Action {
        return object : Action() {

            override var name = this@ActionBuilder.name

            override fun undo() {
                undoAction()
            }

            override fun exec() {
                doAction()
            }

            override var userdata = data
        }
    }
}

fun action(conf: ActionBuilder.() -> Unit): Action {
    return ActionBuilder(conf).build().apply {add();exec();post(ActionExecutedEvent(this))}
}