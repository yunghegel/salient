package org.yunghegel.gdx.utils


import com.badlogic.gdx.Gdx
import kotlinx.coroutines.launch
import ktx.async.KtxAsync
import org.yunghegel.gdx.utils.ext.MutableRef
import org.yunghegel.gdx.utils.ext.ref
import org.yunghegel.gdx.utils.ext.watch
import kotlin.concurrent.thread
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface State<T:Enum<T>> {

    val type : Class<T>

    val state: T

    fun next(): T {
        val values = type.enumConstants
        val nextOrdinal = (state.ordinal + 1) % values.size
        return values[nextOrdinal]
    }

    fun prev(): T {
        val values = type.enumConstants
        val prevOrdinal = (state.ordinal - 1 + values.size) % values.size
        return values[prevOrdinal]
    }

    fun update(delta: Float) : Boolean

    fun transition(to: T)



    val last : Boolean
        get() = state.ordinal == type.enumConstants.size - 1

}



abstract class StateMachine<T>(val initial: T) where T: Enum<T> , T:State<T> {

    open var state: T by MutableRef<T>(initial)

    init {
        watch(::state) { new ->
            new.prev().transition(new)
        }
    }

    open fun shouldUpdate(state: T) : Boolean {
        return !state.last
    }

    fun update(delta: Float) {
        if (shouldUpdate(state)) {
            state = state.next()
        }
        state.update(delta)
    }

    fun next() {
        state = state.next()
    }

    fun prev() {
        state = state.prev()
    }

    val last : Boolean
        get() = state.last


    fun start() {
        KtxAsync.launch {
            while (!state.last) {
                update(Gdx.graphics.deltaTime)
            }
        }
    }


}