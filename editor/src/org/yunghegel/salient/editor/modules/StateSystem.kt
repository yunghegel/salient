package org.yunghegel.salient.editor.modules

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.editor.plugins.rendering.State
import org.yunghegel.salient.engine.helpers.Pools
import org.yunghegel.salient.engine.ui.widgets.notif.notify

/**
 * ECS-powered iterating system that is dedicated to handling a specific state in the rendering pipeline.
 * Functions are carried in by components, owned by an entity which also references a state. The function
 * is executed when the entity's state matches the system's state.
 *
 * The function is executed in the same order every time since we map the enum ordinal to the int priority of the system.
 *  i.e, the enum ordinal is an index into the array of systems
 */
open class StateSystem() : BaseSystem("state_systen",1,Family.all(FunctionComponent::class.java, State::class.java).get()) {

    val doFirsts : MutableList<()->Unit> = mutableListOf()
    val finalizedBy : MutableList<()->Unit> = mutableListOf()
    fun first(func: () -> Unit) {
        doFirsts.add(func)
    }

    fun finalize(func: () -> Unit) {
        finalizedBy.add(func)
    }
    override fun processEntity(p0: Entity?, p1: Float) {
        p0?.let { e ->
            val state = e.getComponent(State::class.java)


            val func = e.getComponent(FunctionComponent::class.java).func

            func(p1)

            e.getComponent(TransitionComponent::class.java)?.transition()

            val autoremoveCondition = e.getComponent(AutoremoveConditionComponent::class.java)
            if (autoremoveCondition != null && autoremoveCondition.condition()) {
                notify("Automaticmoved render routine according to condition")
                engine.removeEntity(e)
                Pools.entityPool.free(e)
            }

        }

        if (p0 is AutoremoveEntiy) {
            engine.removeEntity(p0)
            Pools.entityPool.free(p0)
        }

    }


    override fun update(deltaTime: Float) {
        doFirsts.forEach { it() }
        super.update(deltaTime)
        finalizedBy.forEach { it() }

    }
}