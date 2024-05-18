package org.yunghegel.salient.editor.ui.scene.graph

import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.scene3d.GameObject

class ObjectComponentNode(val component: EntityComponent<*>) : ObjectNode(component.go, ComponentTable(component), component::class.simpleName!!) {

    override val iconName: String = component.iconName




}