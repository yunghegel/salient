package org.yunghegel.salient.engine.tool

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute
import org.yunghegel.gdx.utils.selection.Pickable

typealias TransformEffect = (Matrix4) -> Unit

class ToolHandle(override val id : Int, model : Model) : ModelInstance(model), Pickable {

    data class State(
        var selected: Boolean = false,
        var hovered: Boolean = false,
        var scaleFactor: Float = 0.25f,
        var scl : Float = 0f,
        internal var rotation: Quaternion = Quaternion(),
        internal var position: Vector3 = Vector3(),
        internal var scale: Vector3 = Vector3()
    )

    val state = State()

    override val material: Material = if (materials.size > 0) materials[0] else Material()

    private var tmp : Color? = null
    private var tmpScale = Vector3()
    private var pbr = false

    init {
        encode()
    }

    override fun renderPick(batch: ModelBatch) {
        batch.render(this)
    }

    fun setColor(color: Color) {
        tmp = getColor()
        if (pbr) {
            material.set(PBRColorAttribute.createBaseColorFactor(color))
        } else {
            material.set(ColorAttribute.createDiffuse(color))
        }
    }

    fun getColor() : Color {
        val color : Color
        val attr = material.get(ColorAttribute.Diffuse) ?: material.get(PBRColorAttribute.BaseColorFactor)
        if (attr != null) {

            if (attr is PBRColorAttribute){
                pbr = true
                color = attr.color
            } else {
                val colorAttribute = attr as ColorAttribute
                color = colorAttribute.color
            }

        } else {
            color = Color.WHITE.cpy()
        }
        return color
    }

    fun restoreColor() {
        if (tmp != null) {
            setColor(tmp!!)
        }
    }

    fun applyTransform() {
        transform.set(state.position, state.rotation,state.scale)
    }

    fun update(target: Matrix4, camera: Camera) {
        target.getTranslation(state.position)
        val dst = camera.position.dst(state.position)
        state.scl = dst * state.scaleFactor
        val scaleAmount = Vector3(state.scl, state.scl, state.scl).add(tmpScale)
        state.scale.set(scaleAmount)
        state.rotation.set(target.getRotation(Quaternion()))
        applyTransform()
    }

}