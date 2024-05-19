package org.yunghegel.salient.engine.api.tool

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family


abstract class ComponentTool<T:Component>(name:String,val type:Class<T>) : Tool(name) {




    abstract fun useComponent(component: T)






}