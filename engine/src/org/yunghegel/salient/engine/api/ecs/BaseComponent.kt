package org.yunghegel.salient.engine.api.ecs

import com.badlogic.ashley.core.Component
import org.yunghegel.gdx.utils.data.EnumBitmask
import org.yunghegel.salient.engine.scene3d.SceneContext
import org.yunghegel.salient.engine.system.debug
import kotlin.reflect.KClass

abstract class BaseComponent() : Component, Cloneable {

    enum class Routine {
        DEBUG,
        DEPTH,
        COLOR
    }

    abstract val type : KClass<out BaseComponent>

    internal val debug = Routine.DEBUG
    internal val depth = Routine.DEPTH
    internal val color = Routine.COLOR


    fun interface Predicate {
        fun evaluate() : Boolean
    }

    interface Listener {
        fun onComponentAdded(entity: ObjectEntity)
        fun onComponentRemoved(entity: ObjectEntity)
        class Builder(builder: Builder.() -> Unit) {


            var added: (ObjectEntity) -> Unit = {}
            var removed: (ObjectEntity) -> Unit = {}

            init {
                builder()
            }

            fun build() = object : Listener {
                override fun onComponentAdded(entity: ObjectEntity) = added(entity)
                override fun onComponentRemoved(entity: ObjectEntity) = removed(entity)
            }
        }
    }

    private val listeners = mutableListOf<Listener>()

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun addListener(builder: Listener.Builder.() -> Unit) {
        listeners.add(Listener.Builder(builder).build())
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }


    private val preconditions : Map<Routine,MutableList<Predicate>> = mapOf(
        Routine.DEBUG to mutableListOf(),
        Routine.DEPTH to mutableListOf(),
        Routine.COLOR to mutableListOf()
    )

    val implementations = EnumBitmask(Routine::class.java)

    internal fun depthCondition(predicate: Predicate) {
        preconditions[Routine.DEPTH]!!.add(predicate)
    }

    internal fun debugCondition(predicate: Predicate) {
        preconditions[Routine.DEBUG]!!.add(predicate)
    }

    internal fun colorCondition(predicate: Predicate) {
        preconditions[Routine.COLOR]!!.add(predicate)
    }

    internal fun implements(vararg routines: Routine) {
        routines.forEach { implementations.set(it,true) }
    }

    val implDebug : Boolean
        get() = implementations.get(Routine.DEBUG)

    val implDepth : Boolean
        get() = implementations.get(Routine.DEPTH)

    val implColor : Boolean
        get() = implementations.get(Routine.COLOR)


    init {
        preconditions[Routine.DEBUG]!!.add(Predicate { implementations.get(Routine.DEBUG) })
        preconditions[Routine.DEPTH]!!.add(Predicate { implementations.get(Routine.DEPTH) })
        preconditions[Routine.COLOR]!!.add(Predicate { implementations.get(Routine.COLOR) })
    }

    fun onComponentAdded(entity: ObjectEntity) {
        debug("Component added: ${this::class.simpleName}")
        listeners.forEach { it.onComponentAdded(entity) }
    }

    fun onComponentRemoved(entity: ObjectEntity) {
        debug("Component removed: ${this::class.simpleName}")
        listeners.forEach { it.onComponentRemoved(entity) }
    }

    open val renderer : Boolean = false

    open val updater : Boolean = false

    val shouldRenderDebug : Boolean
        get() = preconditions[Routine.DEBUG]!!.all { it.evaluate() }

    val shoulRenderDepth : Boolean
        get() = preconditions[Routine.DEPTH]!!.all { it.evaluate() }

    val shouldRenderColor : Boolean
        get() = preconditions[Routine.COLOR]!!.all { it.evaluate() }


    context(SceneContext)
    open fun renderDebug(delta:Float) {}

    context(SceneContext)
    open fun renderDepth(delta: Float) {}

    context(SceneContext)
    open fun renderColor(delta: Float) {}
}