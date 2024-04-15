package org.yunghegel.salient.engine.tool

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family


abstract class ComponentTool(name: String, activator: Family) : Tool(name) {


    init {
        this.activator = activator
    }

    private val assignableFor: Class<out Component?>? = null // TODO remove this in favor to allowed method


    protected abstract fun createComponent(entity: Entity?): Component?


}