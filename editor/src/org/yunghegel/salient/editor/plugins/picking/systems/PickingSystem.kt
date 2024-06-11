package org.yunghegel.salient.editor.plugins.picking.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import ktx.ashley.has
import ktx.collections.GdxArray
import org.yunghegel.gdx.utils.selection.Pickable
import org.yunghegel.gdx.utils.selection.Picker
import org.yunghegel.gdx.utils.selection.PickerShader
import org.yunghegel.salient.editor.app.salient
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.editor.scene.GameObjectSelectionManager
import org.yunghegel.salient.engine.api.VIEWPORT_SOURCE
import org.yunghegel.salient.engine.api.flags.SELECTED
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.ModelRenderable
import org.yunghegel.salient.engine.scene3d.component.PickableComponent
import org.yunghegel.salient.engine.system.debug
import org.yunghegel.salient.engine.system.inject

class PickingSystem : BaseSystem("picking_system",8,Family.all(PickableComponent::class.java).get()) {



    val pickables : MutableList<Pickable> = mutableListOf()

    val batch : ModelBatch = ModelBatch(PickerShader)

    val cam: PerspectiveCamera = inject()

    val viewport : Viewport = inject()

    private var getPick = false

    private var tmp: Vector2 = Vector2()

    private var button = -1
    private var key = -1
    private var double = false


    val selectionManager : GameObjectSelectionManager = inject()

    var listeners = mutableListOf<PickListener>()

    init {
        listeners += (PickListener { pick , removed ->
            if (pick is PickableComponent)
            {
                if (removed) {
                    println("Deselected ${pick.go}")
                } else {
                    println("Selected ${pick.go}")
                }
            }
        })
    }

    override fun update(deltaTime: Float) {
//        getPick == true when we invoke a button click; otherwise dont bother gathering pickables
        if (!getPick) return
//        clear old list of candidates
        pickables.clear()
//        generate new list of candidates
        super.update(deltaTime)
//        pick the candidate
        val pick = picker.pick(viewport,batch,cam,tmp.x.toInt(),tmp.y.toInt(),pickables)

        if (pick!=null) handleSelection(pick)
//        reset the system
        reset()
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val candidates = entity?.components?.filterIsInstance<PickableComponent>() ?: emptyList()
        pickables.addAll(candidates)
    }

    fun handleSelection(pick: Pickable) {
        if (pick is PickableComponent) {
            val go = pick.go
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                if(selectionManager.select(go,true)) {
                    listeners.forEach { it.pickChanged(pick,false) }
                }
            } else {
                if (selectionManager.selection.contains(go) && (double or Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))) {
                    if(selectionManager.deselect(go)) {
                        listeners.forEach { it.pickChanged(pick,true) }
                    }
                } else if(!Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    if (selectionManager.select(go)) {
                        listeners.forEach { it.pickChanged(pick,false) }
                    }
                }
            }
        }
    }

    fun pick(x:Float,y:Float,button:Int,key:Int,double:Boolean) {
        getPick = true
        this.button = button
        this.key = key
        this.double = double
        tmp.set(x,y)
    }

    fun reset() {
        getPick = false
        button = -1
        key = -1
        double = false
    }


    fun interface PickListener {
        fun pickChanged(picked:Pickable, removed:Boolean)
    }

    companion object {
        val picker = Picker()
    }

}