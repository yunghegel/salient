package org.yunghegel.salient.engine.graphics.scene3d.component

import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.graphics.scene3d.GameObject

class SelectedComponent(go: GameObject) : EntityComponent<GameObject>(null,go,go)