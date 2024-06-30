package org.yunghegel.salient.editor.plugins.gizmos.lib

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import org.yunghegel.gdx.utils.selection.Pickable


abstract class GizmoHandle<T>(val model: Model, uid : Int) : Pickable {

    var position: Vector3 = Vector3()
    var scale:Vector3 = Vector3(1f, 1f, 1f)
    var rotation: Quaternion = Quaternion()
    var tmpScale = Vector3(0f,0f,0f)
    var scaleFactor: Float = 0.3f
    var startColor : Color? =  null

    override val id: Int = uid

    val instance = ModelInstance(model)
    override val material: Material = instance.materials.get(0)




    init {
        encode()
    }

    override fun whenPicked(previousPick: Pickable?) {

    }

    override fun renderPick(batch: ModelBatch) {
        batch.render(instance)
    }

    abstract fun render(batch: ModelBatch)

    abstract fun update(delta: Float, target: T?)

    fun setColor(color: Color?) {
        startColor = getColor()
        material.set(ColorAttribute.createDiffuse(color))
    }

    fun restoreColor() {
        if (startColor == null) {
            return
        }
        material.set(ColorAttribute.createDiffuse(startColor))
    }

    fun getColor(): Color? {
        val diffuse: ColorAttribute =material.get(ColorAttribute.Diffuse) as ColorAttribute? ?: return null
        return diffuse.color
    }



}