package org.yunghegel.salient.engine.api.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Predicate
import org.yunghegel.gdx.utils.data.EnumBitmask
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.salient.engine.api.properties.Type
import org.yunghegel.salient.engine.api.properties.Typed
import org.yunghegel.salient.engine.scene3d.GameObject
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

    open fun onComponentAdded(go: GameObject) {debug("Component added: ${this::class.simpleName}")}

    open fun onComponentRemoved(go: GameObject) {}

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