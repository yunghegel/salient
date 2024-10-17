package org.yunghegel.salient.editor.ui.scene.inspector.component

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.editor.ui.scene.inspector.ComponentInspector
import org.yunghegel.salient.engine.events.scene.onGameObjectSelected
import org.yunghegel.salient.engine.scene3d.component.TransformComponent
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.widgets.value.widgets.LabeledFloatField

class TransformInspector() : ComponentInspector<TransformComponent, Matrix4>(TransformComponent::class.java,"Transform","transform_object") {

    constructor(component: TransformComponent) : this() {
        populate(component)
    }

    init {
        onGameObjectSelected {
            it.go.firstOrNull()?.let { go ->
                val transform = go.getComponent(TransformComponent::class.java)
                if (transform != null) {
                    populate(transform)
                }
            }
        }
    }
    
    

    companion object {
        private val tempV3 = Vector3()
        private val tempQuat = Quaternion()
    }

    private val FIELD_SIZE = 70
    private val posX = LabeledFloatField("x", FIELD_SIZE)
    private val posY = LabeledFloatField("y", FIELD_SIZE)
    private val posZ = LabeledFloatField("z", FIELD_SIZE)

    private val rotX = LabeledFloatField("x", FIELD_SIZE)
    private val rotY = LabeledFloatField("y", FIELD_SIZE)
    private val rotZ = LabeledFloatField("z", FIELD_SIZE)

    private val sclX = LabeledFloatField("x", FIELD_SIZE)
    private val sclY = LabeledFloatField("y", FIELD_SIZE)
    private val sclZ = LabeledFloatField("z", FIELD_SIZE)
    
    val pos : Vector3
        get() = Vector3(posX.float,posY.float,posZ.float)
    
    val rot : Quaternion
        get() = Quaternion().setEulerAngles(rotX.float,rotY.float,rotZ.float)
    
    val scl : Vector3
        get() = Vector3(sclX.float,sclY.float,sclZ.float)

    init {
        align(Align.center)

        posX.text = "0.00"
        posY.text = "0.00"
        posZ.text = "0.00"
        rotX.text = "0.00"
        rotY.text = "0.00"
        rotZ.text = "0.00"
        sclX.text = "1.00"
        sclY.text = "1.00"
        sclZ.text = "1.00"
        val posTable = STable()
        with(posTable) {
            add(SLabel("Position: ")).pad(5f).row()
            add(posX).padRight(2f).padLeft(2f)
            add(posY).padRight(2f).padLeft(2f)
            add(posZ).padRight(2f).padLeft(2f)
            add(SImageButton("link")).padHorizontal(2f).size(16f).row()
        }
        add(posTable).pad(5f).center().growX().row()

        val rotTable = STable()
        with(rotTable) {
            add(SLabel("Rotation: ")).pad(5f).row()
            add(rotX).padRight(2f).padLeft(2f)
            add(rotY).padRight(2f).padLeft(2f)
            add(rotZ).padRight(2f).padLeft(2f)
            add(SImageButton("link")).padHorizontal(2f).size(16f).row()
        }
        add(rotTable).pad(5f).center().growX().row()

        val sclTable = STable()
        with(sclTable) {
            add(SLabel("Scale: ")).pad(5f).row()
            add(sclX).padRight(2f).padLeft(2f)
            add(sclY).padRight(2f).padLeft(2f)
            add(sclZ).padRight(2f).padLeft(2f).padTop(5f)
            add(SImageButton("link")).padHorizontal(2f).size(16f).row()
        }
        add(sclTable).pad(5f).growX().center().row()
        buildListeners()
    }
    
    private fun buildListeners(){
        posX.onChange {
            current ?: return@onChange
            current!!.setTranslation(pos)
        }
        posY.onChange {
            current ?: return@onChange
            current!!.setTranslation(pos)
        }
        posZ.onChange {
            current!!.setTranslation(pos)
        }
        
        rotX.onChange {
            current ?: return@onChange
            current!!.setRotation(rot)
        }
        rotY.onChange {
            current ?: return@onChange
            current!!.setRotation(rot)
        }
        rotZ.onChange {
            current ?: return@onChange
            current!!.setRotation(rot)
        }

        
        sclX.onChange {
            current ?: return@onChange
            current!!.setScale(scl)
        }
        sclY.onChange {
            current ?: return@onChange
            current!!.setScale(scl)
        }
        sclZ.onChange {
            current ?: return@onChange
            current!!.setScale(scl)
        }
    }

    override fun createLayout() {

    }

    override fun populate(component: TransformComponent?) {
        component ?: return
        val transform = component.value ?: return
        transform.getTranslation(pos)
        transform.getRotation(tempQuat)
        transform.getScale(scl)
        rotX.text = tempQuat.getPitch().toString()
        rotY.text = tempQuat.getYaw().toString()
        rotZ.text = tempQuat.getRoll().toString()
        posX.text = pos.x.toString()
        posY.text = pos.y.toString()
        posZ.text = pos.z.toString()
        sclX.text = scl.x.toString()
        sclY.text = scl.y.toString()
        sclZ.text = scl.z.toString()

    }


}