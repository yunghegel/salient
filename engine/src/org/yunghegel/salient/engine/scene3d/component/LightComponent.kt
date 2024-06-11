package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.graphics.g3d.environment.BaseLight
import ktx.collections.GdxArray
import org.yunghegel.salient.engine.ui.Icon
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.ui.icon
import org.yunghegel.salient.engine.scene3d.GameObject

class LightComponent(lights: GdxArray<BaseLight<*>>,go: GameObject) : EntityComponent<GdxArray<BaseLight<*>>>(lights,go) {

    constructor(light: BaseLight<*>,go: GameObject) : this(GdxArray.with(light),go)

    override val iconName: String = "directonal_light"

    override val type = LightComponent::class
}