package org.yunghegel.salient.engine.api.tool

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity

class ToolEntity(tool : Tool) : Entity() {
    init {
        add(ToolComponent(tool))
    }
}

@JvmInline
value class ToolComponent(val tool : Tool) : Component