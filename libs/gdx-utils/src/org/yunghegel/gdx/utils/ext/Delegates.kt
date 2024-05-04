package org.yunghegel.gdx.utils.ext

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


fun <T:Any> notnull() : ReadWriteProperty<Any?, T> = Delegates.notNull<T>()

typealias ChangeListener<T> = (T,T)->Unit

class Observable<T>(private var value: T, private val listeners: MutableList<(T) -> Unit> = mutableListOf()) : ReadWriteProperty<Any?,T> {

    fun addListener(listener: (T) -> Unit) = listeners.add(listener)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
        listeners.forEach { it(value) }
    }
}

class LazyMutable<T>(val initializer: () -> T) : ReadWriteProperty<Any?, T> {
    private object UNINITIALIZED_VALUE
    private var prop: Any? = UNINITIALIZED_VALUE

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return if (prop == UNINITIALIZED_VALUE) {
            synchronized(this) {
                return if (prop == UNINITIALIZED_VALUE) initializer().also { prop = it } else prop as T
            }
        } else prop as T
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        synchronized(this) {
            prop = value
        }
    }
}

fun <T> lazyMutable(initializer: () -> T) = LazyMutable(initializer)

fun <T> observable(value: T, onChange: (T,T)->Unit) = Observable(value)

