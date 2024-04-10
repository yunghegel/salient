package org.yunghegel.salient.engine.api.undo

import com.badlogic.gdx.utils.Disposable
import ktx.collections.GdxArray
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.history.ActionExecutedEvent
import org.yunghegel.salient.engine.events.history.ActionUndoneEvent
import org.yunghegel.salient.engine.io.debug

class ActionHistory(private val limit: Int) {

     var pointer: Int = 0
        private set
    private val commands: GdxArray<Action> = GdxArray(limit)

    init {
        pointer = -1
    }

    fun add(command: Action): Int {
        debug("Adding command to history")
        if (size() == 0) {
            commands.add(command)
            pointer++
            return pointer
        }

        if (pointer < size() - 1) {
            removeCommands(pointer + 1, commands.size - 1)
            commands.add(command)
            pointer++
        } else {
            if (size() == limit) {
                removeCommand(0)
                commands.add(command)
            } else {
                commands.add(command)
                pointer++
            }
        }


        return pointer
    }

    private fun removeCommand(index: Int) {
        val cmd = commands.get(index)
        if (cmd is Disposable) {
            cmd.dispose()
        }
        commands.removeIndex(index)
    }

    private fun removeCommands(from: Int, to: Int) {
        for (i in from..to) {
            val cmd = commands.get(i)
            if (cmd is Disposable) {
                cmd.dispose()
            }
        }

        commands.removeRange(from, to)
    }

    fun goBack(): Int {
        if (pointer >= 0) {
            commands.get(pointer).undo()
            post(ActionUndoneEvent(commands.get(pointer)))
            pointer--
        }

        return pointer
    }

    fun goForward(): Int {
        if (pointer < commands.size - 1) {
            pointer++
            commands.get(pointer).exec()
            post(ActionExecutedEvent(commands.get(pointer)))
        }

        return pointer
    }

    fun getNext(): Action? {
        return if (pointer < commands.size - 1) {
            commands.get(pointer + 1)
        } else {
            null
        }
    }

    fun getPrevious(): Action? {
        return if (pointer >= 0) {
            commands.get(pointer)
        } else {
            null
        }
    }

    fun isRedoPossible(): Boolean {
        return pointer < commands.size - 1
    }

    fun isUndoPossible(): Boolean {
        return pointer >= 0
    }

    fun clear() {
        commands.filterIsInstance<Disposable>()
            .forEach { it.dispose() }
        commands.clear()
        pointer = -1
    }

    fun size(): Int {
        return commands.size
    }

    companion object {

       var DEFAULT_LIMIT = 50
    }

}