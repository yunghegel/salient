package org.yunghegel.salient.engine.graphics.scene3d.component

import com.badlogic.gdx.graphics.g3d.environment.BaseLight
import ktx.collections.GdxArray
import org.yunghegel.salient.engine.api.Icon
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.api.icon
import org.yunghegel.salient.engine.graphics.scene3d.GameObject

class LightComponent(lights: GdxArray<BaseLight<*>>,go: GameObject) : EntityComponent<GdxArray<BaseLight<*>>>(null, lights,go),
    Icon by icon("directional_light") {
    constructor(light: BaseLight<*>,go: GameObject) : this(GdxArray.with(light),go)
}