package org.yunghegel.salient.engine.api

import org.yunghegel.gdx.utils.ext.MapBuilder


/**
 * A [StateRecovery] interface is used to map a predefined set of options to instructions in a state recovery operation.
 * Each option is represented by an enum value and a lambda function that is executed when the option is present.
 * A collection of [T] values is saved in the owner object and used to recover the state of the object.
 */
interface StateRecovery<T:Enum<T>> {

    val actionMap : MutableMap<T,(Map<String,String>)->Unit>

    val instructions : MutableList<Payload<T>>


    fun recoverState(vararg input: Payload<T>) : Result<Unit> {
        input.forEach { payload ->
            actionMap[payload.stage]?.invoke(payload.args) ?: return Result.failure(Exception("No instruction found for ${payload.stage}"))
        }
        return Result.success(Unit)
    }

    fun defineInstruction(stage: T, instruction: (Map<String,String>)->Unit) {
        actionMap[stage] = instruction
    }

    class Payload<T:Enum<T>>(val stage: T, val args : Map<String,String>) {

        operator fun get(key: String) : String {
            return args[key] ?: ""
        }

        fun has(key: String) : Boolean {
            return args.containsKey(key)
        }
    }

    class Builder<T:Enum<T>> {
        val payloads = mutableListOf<Payload<T>>()
        fun add(stage: T, args: MapBuilder<String, String>.() -> Unit) {
            val map = MapBuilder<String, String>().apply(args).build()
            payloads.add(Payload(stage, map))
        }
    }

}

