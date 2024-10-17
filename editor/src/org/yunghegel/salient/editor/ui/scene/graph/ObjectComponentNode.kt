package org.yunghegel.salient.editor.ui.scene.graph

import org.yunghegel.salient.engine.api.ecs.EntityComponent

class ObjectComponentNode(val component: EntityComponent<*>) : ObjectNode(component.go, ComponentTable(component), component::class.simpleName!!) {

    override val iconName: String = component.iconName

    init {
        nodeName = component::class.simpleName!!.substringBefore("Component")
    }




}