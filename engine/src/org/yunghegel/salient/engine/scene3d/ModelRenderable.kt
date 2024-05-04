package org.yunghegel.salient.engine.scene3d

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import org.yunghegel.gdx.utils.selection.PickableModel

class ModelRenderable(val model : ModelInstance, override val id : Int) : PickableModel(model) {

    init {
        encode()
    }

}