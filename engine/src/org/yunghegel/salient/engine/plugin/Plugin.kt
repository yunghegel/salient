package org.yunghegel.salient.engine.plugin

import com.badlogic.ashley.core.EntitySystem
import ktx.inject.Context
import org.yunghegel.salient.engine.api.Initializable
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.tool.Tool

interface Plugin : Initializable, Named {

    val systems: MutableList<System<*,*>>

    val tools: MutableList<Tool>

    val registry : Context.()->Unit

}