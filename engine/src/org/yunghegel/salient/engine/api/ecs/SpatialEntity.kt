package org.yunghegel.salient.engine.api.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import kotlin.reflect.KClass

const val VISIBLE = 1
const val SELECTED = 2
const val CULLED = 4
const val DIRTY = 8
const val DEBUG_ALL = 16
const val LOCKED = 32


@Suppress("UNCHECKED_CAST")
open class SpatialEntity : Entity() {

    val entityComponents : MutableMap<Class<out BaseComponent>,BaseComponent> = mutableMapOf()

    init {
        flags = VISIBLE
    }

    operator fun <T:BaseComponent> get(type: KClass<T>): T? {
        return entityComponents[type.java] as T?
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

    fun <T:BaseComponent> getComponent(type: Class<out T>): T? {
        return entityComponents[type] as T?
    }

    fun <T:BaseComponent> checkComponent(type: Class<T>): Boolean {
        return entityComponents.containsKey(type)
    }

    override fun add(component: Component): Entity {
        if (component is BaseComponent) {
            entityComponents[component.javaClass] = component
        }
        return super.add(component)
    }

    override fun <T : Component> remove(componentClass: Class<T>?): T? {
        val item = super.remove(componentClass)
        if (item is BaseComponent) {
            entityComponents.remove(item.javaClass)
        }
        return item
    }

    override fun removeAll() {
        entityComponents.clear()
        super.removeAll()
    }

}