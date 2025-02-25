package org.yunghegel.salient.engine.api.plugin

import org.yunghegel.salient.engine.system.InjectionContext
import org.yunghegel.salient.engine.api.properties.Initializable
import org.yunghegel.gdx.utils.data.Named
import org.yunghegel.salient.engine.api.ecs.System
import org.yunghegel.salient.engine.api.tool.InputTool
import org.yunghegel.salient.engine.api.tool.Tool

interface Plugin : Initializable, Named {

    val systems: MutableList<System<*,*>>

    val tools: MutableList<Tool>

    val registry : InjectionContext.()->Unit

}