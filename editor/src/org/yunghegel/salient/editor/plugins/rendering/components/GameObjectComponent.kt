package org.yunghegel.salient.editor.plugins.rendering.components

import org.yunghegel.salient.engine.api.ecs.BaseComponent
import org.yunghegel.salient.engine.scene3d.GameObject
import kotlin.reflect.KClass

class GameObjectComponent(val gameObject : GameObject) : BaseComponent() {

    override val type: KClass<out BaseComponent> = GameObjectComponent::class

}