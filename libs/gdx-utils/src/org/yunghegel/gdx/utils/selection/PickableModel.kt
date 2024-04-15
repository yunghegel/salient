package org.yunghegel.gdx.utils.selection

import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance

abstract class PickableModel(modelInstance: ModelInstance?) : ModelInstance(modelInstance), Pickable {
    override val material: Material
        get() = materials.first()

    override fun renderPick(batch: ModelBatch?) {
        batch!!.render(this)
    }
}
