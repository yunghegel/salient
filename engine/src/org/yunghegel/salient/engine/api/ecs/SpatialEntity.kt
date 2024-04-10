package org.yunghegel.salient.engine.api.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity

const val VISIBLE = 1
const val SELECTED = 2
const val CULLED = 4
const val DIRTY = 8
const val DEBUG_ALL = 16
const val LOCKED = 32


@Suppress("UNCHECKED_CAST")
open class SpatialEntity : Entity() {

    val entityComponents : MutableMap<Class<*>, EntityComponent<*>> = mutableMapOf()

    init {
        flags = VISIBLE
    }

    fun checkFlag(flag: Int): Boolean {
        return flags and flag != 0
    }

    fun setFlag(flag: Int) {
        flags = flags or flag
    }

    fun clearFlag(flag: Int) {
        flags = flags and flag.inv()
    }

    fun setFlag(flag: Int, value: Boolean) {
        if (value) {
            setFlag(flag)
        } else {
            clearFlag(flag)
        }
    }

    fun check(flags: Int): Boolean {
        return this.flags and flags == flags
    }

    fun <T> getComponent(type: Class<T>): T? {
        return entityComponents[type]?.value as T?
    }

    fun <T> checkComponent(type: Class<T>): Boolean {
        return entityComponents.containsKey(type)
    }

    override fun add(component: Component): Entity {
        if (component is EntityComponent<*>) {
            if (component.type!=null)
            entityComponents[component.type] = component
        }
        return super.add(component)
    }

    override fun <T : Component?> remove(componentClass: Class<T>?): T {
        if (componentClass != null) {
            entityComponents.remove(componentClass)
        }
        return super.remove(componentClass)
    }

    override fun removeAll() {
        entityComponents.clear()
        super.removeAll()
    }

}