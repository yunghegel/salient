package org.yunghegel.salient.engine.api.tool

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity


abstract class ComponentTool<T : Component>(name: String, val type: Class<T>) : InputTool(name) {




    abstract fun useComponent(component: T,entity: Entity)

//    function to get another component from the entity
    fun <U : Component> fetchComponent(entity: Entity, componentType: Class<U>): U? {
        return entity.getComponent(componentType)
    }


}