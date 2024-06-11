package org.yunghegel.salient.engine.scene3d

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import org.yunghegel.gdx.utils.selection.PickableModel

class ModelRenderable(val model : ModelInstance, val go: GameObject,) : PickableModel(model) {
    override val id : Int get() = go.id
    init {
        encode()
    }

}