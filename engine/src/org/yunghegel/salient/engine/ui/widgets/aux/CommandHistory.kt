package org.yunghegel.salient.engine.ui.widgets.aux

import com.badlogic.gdx.Input

class CommandHistory {

    val history = mutableListOf<String>()

    @Transient
    private var index = 0

    var MAXIMUM_HISTORY_LENGTH = 50

    fun add(command: String) {
        history.add(command)
        index = history.size
        validateHistory()
    }

    fun validateHistory() {
        if (history.size > MAXIMUM_HISTORY_LENGTH) {
            history.removeAt(0)
        }
    }

    fun getPrevious(): String? {
        if (index > 0) {
            index--
            return history[index]
        }
        return null
    }

    fun getNext(): String? {
        if (index < history.size - 1) {
            index++
            return history[index]
        }
        return null
    }

    fun clear() {
        history.clear()
        index = 0
    }

    @Transient
    var submitInput = {input: String -> add(input)}

    @Transient
    var keyListener = {current:String, key: Int ->
        when (key) {
            Input.Keys.UP -> getPrevious()
            Input.Keys.DOWN -> getNext()
            else -> current
        }
    }

}