package org.yunghegel.salient.engine.api.tool

import com.badlogic.ashley.core.Component


abstract class ComponentTool<T : Component>(name: String, val type: Class<T>) : InputTool(name) {




    abstract fun useComponent(component: T)






}