package org.yunghegel.salient.engine.api.ecs

import org.yunghegel.salient.engine.api.Tagged

class TagsComponent : Tagged, BaseComponent() {
    override val type = TagsComponent::class
    override val tags = mutableSetOf<String>()
}