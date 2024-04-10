package org.yunghegel.salient.editor.plugins.selection.lib

import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelBatch

interface Pickable {
    val iD: Int

    val material: Material

    fun renderPick(batch: ModelBatch?)

    fun encode() {
        val attr = PickerColorEncoder.encodeRaypickColorId(iD)
        material.set(attr)
    }
}
