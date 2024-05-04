package org.yunghegel.salient.engine.scene3d.component

import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.api.flags.SELECTED
import org.yunghegel.salient.engine.scene3d.GameObject

class SelectedComponent(go: GameObject) : EntityComponent<GameObject>(go,go) {

    override fun onComponentAdded(go: GameObject) {
        go.set(SELECTED)
    }

    override fun onComponentRemoved(go: GameObject) {
        go.clear(SELECTED)
    }

}