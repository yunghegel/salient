package org.yunghegel.gdx.utils.selection

import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelBatch

interface Pickable {

    val id: Int

    val material: Material

    fun renderPick(batch: ModelBatch?)

    fun encode() {
        val attr = PickerColorEncoder.encodeRaypickColorId(id)
        material.set(attr)
    }
}
