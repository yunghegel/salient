package org.yunghegel.gdx.utils.ext

import java.lang.reflect.Field
import java.util.concurrent.locks.ReentrantLock
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.isAccessible


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


private typealias Effect = () -> Unit
private typealias Getter<T> = () -> T
private typealias Reactor<T> = (T) -> Unit

private val lock = ReentrantLock()
private var atomicEffect: Effect? = null

private var atomicReact: Reactor<*>? = null


internal interface Gettable<T> {

    operator fun getValue(thisRef: Any, property: KProperty<*>): T?
}

internal interface Settable<T> {

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: T)
}

abstract class Ref<T> : Gettable<T> {

    val subscribers = HashSet<Effect>()

    protected fun <T> track() {
        atomicEffect?.let { subscribers.add(it) }
    }

    protected fun <T> trigger() {
        /* TODO: de-duplicate effects */
        subscribers.forEach { it() }
    }
}

open class MutableRef<T : Any>(initial: T) : Ref<T>(), Settable<T>, ReadWriteProperty<Any, T> {

    var _value = initial

    override operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        track<T>()
        return _value
    }

    override operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        _value = value
        trigger<T>()
    }

}




class Computed<T>(
    private val getter: Getter<T>
) : Ref<T>() {

    override operator fun getValue(thisRef: Any, property: KProperty<*>): T {
        track<T>()
        return getter()
    }
}

fun watchEffect(update: Effect) {
    lateinit var effect: Effect
    effect = {
        lock.lock()
        try {
            atomicEffect = effect
            update()
            atomicEffect = null
        } finally {
            lock.unlock()
        }
    }

    effect()
}

fun <T : Any> KMutableProperty0<T>.watch(effect: () -> Unit): MutableRef<T>? {
    // Use "isAccessible = true" to make the property accessible
    isAccessible = true

    var d = this.getDelegate()
    val delegate = this.getDelegate() as? MutableRef<T>
    if (delegate != null) {
        delegate.subscribers.add(effect)
        return delegate
    } else {
        val obj = this.get()

    }
    return null
}

fun <T : Any> T.watcheffects(effect: (T) -> Unit) {
    // Use "isAccessible = true" to make the property accessible
    watchEffect {
        effect(this)
    }

}

fun fieldIsDelegate(field: Field) = field.type.isAssignableFrom(Ref::class.java)

fun findDelegate(owner: Any, of: Any): Any? {

    val fields = owner::class.java.declaredFields
    fields.forEach { field ->
        if (fieldIsDelegate(field)) {
            field.isAccessible = true
            val delegate = field.get(owner)
            if (delegate == of) {
                return field
            }
        }
    }
    return null
}

fun <T:Any> Any.watch(fieldName: String, effect: (T) -> Unit) {
    this::class.java.declaredFields.forEach { field ->
        println(field.name)
    }
    val field = this::class.java.getDeclaredField("$fieldName\$delegate")
    field.isAccessible = true
    val delegate = field.get(this)
    if (delegate is MutableRef<*>) {
        delegate.subscribers.add({ effect(delegate._value as T) })
    }
}

fun <T:Any> watch(prop: KMutableProperty0<T>, effect: (T) -> Unit) {
    prop.isAccessible = true
    (prop.getDelegate() as? MutableRef<T>?)?.subscribers?.add({ effect(prop.get()) })
}

fun <T> computed(getter: Getter<T>) = Computed(getter)

inline fun <reified T : Any> ref(initial: T) = MutableRef(initial)

class TrackedStateVar<T:Any>(initial: T) : MutableRef<T>(initial) {



    override operator fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        _value = value
        trigger<T>()
    }
}






