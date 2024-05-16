package org.yunghegel.salient.editor.plugins.picking.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import org.yunghegel.gdx.utils.ext.getBounds
import org.yunghegel.gdx.utils.ext.transformInput

import org.yunghegel.gdx.utils.selection.Pickable
import org.yunghegel.gdx.utils.selection.Picker
import org.yunghegel.gdx.utils.selection.PickerShader
import org.yunghegel.salient.editor.app.Gui
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.engine.scene3d.component.PickableComponent
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.UI.viewportToScreen

class HoverSystem() : BaseSystem("picking_system",8, Family.all(PickableComponent::class.java).get()) {

    val picker = Picker()

    val pickables : MutableList<Pickable> = mutableListOf()

    var processHover = false

    var action : ((Pickable?)->Unit)? = null

    val batch : ModelBatch = ModelBatch(PickerShader)
    val cam: PerspectiveCamera = inject()
    val viewport : Viewport = inject()

    var pick : Pickable? = null

    var x = 0
    var y = 0

    override fun update(deltaTime: Float) {
        if(processHover) {
            processHover = false
            super.update(deltaTime)
            (UI).viewport.apply()
            pick = picker.pick(viewport,batch,cam,x,y,pickables)
            pick?.let { picked ->
                action?.invoke(picked)
            }
            action = null
        }

        pickables.clear()
    }

    override fun processEntity(p0: Entity?, p1: Float) {
        val candidates = p0?.components?.filterIsInstance<PickableComponent>() ?: emptyList()
        pickables.addAll(candidates)
    }
    val current = Rectangle()
    val target = Rectangle()
    fun hover(x: Int, y: Int, action : (Pickable?)->Unit) : Pickable? {
        processHover = true
        val transform = (UI.root as Gui).localToScreenCoordinates(Vector2(x.toFloat(),y.toFloat()))
        this.x = transform.x.toInt()
        this.y = transform.y.toInt()
        this.action = action
        return pick
    }


}