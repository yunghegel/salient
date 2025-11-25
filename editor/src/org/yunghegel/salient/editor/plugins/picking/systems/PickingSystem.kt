package org.yunghegel.salient.editor.plugins.picking.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport
import org.yunghegel.gdx.utils.ext.delta
import org.yunghegel.gdx.utils.selection.Pickable
import org.yunghegel.gdx.utils.selection.Picker
import org.yunghegel.gdx.utils.selection.PickerShader
import org.yunghegel.salient.editor.modules.buffers
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.editor.plugins.intersect.tools.IntersectorTool
import org.yunghegel.salient.editor.plugins.picking.PickablesBag
import org.yunghegel.salient.editor.scene.GameObjectSelectionManager
import org.yunghegel.salient.engine.scene3d.component.PickableComponent
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.UI

open class PickingSystem : BaseSystem("picking_system",8,Family.one(PickableComponent::class.java, PickablesBag::class.java).get()) {

    val picker = Picker()

    val pickables : MutableList<Pickable> = mutableListOf()

    val cam: PerspectiveCamera = inject()

    val viewport : Viewport = inject()

    private var getPick = false

    private var tmp: Vector2 = Vector2()

    private var button = -1
    private var key = -1
    private var double = false

    var updateHoverFbo = false
    var hoverPM : Pixmap? = null


    val selectionManager : GameObjectSelectionManager = inject()

    var listeners = mutableListOf<PickListener>()

    var exec : ((Pickable)->Unit)? = null



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
        buffers["picking_buffer"] = picker.fbo
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



        if (pick!=null){
            if(exec != null) exec?.invoke(pick)
            else handleSelection(pick)
        }
//        reset the system
        reset()
    }

    override fun processEntity(entity: Entity?, deltaTime: Float) {
        val candidates = entity?.components?.filterIsInstance<PickableComponent>() ?: emptyList()
        pickables.addAll(candidates)
        val bag = entity?.getComponent(PickablesBag::class.java)
        if (bag != null ) {
            val (x,y) = UI.viewportToScreen(Gdx.input.x,Gdx.input.y)

            val pick = picker.pick(viewport,batch,cam,x.toInt(),y.toInt(),bag.pickables)
            if (pick!=null){
                bag.picked(pick)
                println("picked from system")
            }
        }
    }

    private fun handleSelection(pick: Pickable) {
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

    fun pick(x:Float,y:Float,button:Int,key:Int,double:Boolean, action:((Pickable)->Unit)? = null) {
        getPick = true
        this.exec = action
        this.button = button
        this.key = key
        this.double = double
        val coords = UI.viewportToScreen(x.toInt(),y.toInt())
        tmp.set(coords.first,coords.second)
    }

    fun pick(x:Float,y:Float,pickables: List<Pickable>,cb: (Int)->Unit= {}, buffersize: Int = 0) : Pickable? {
        val coords = UI.viewportToScreen(x.toInt(),y.toInt())
        val picked = picker.pick(viewport,batch,cam,coords.first.toInt(),coords.second.toInt(),pickables, buffersize = buffersize)
        val id = picked?.id ?: -1
        cb(id)
        return picked
    }



    fun hover(x:Int,y:Int,refresh:Boolean) : Pickable? {
        if (hoverPM == null || refresh) {
            super.update(delta)
            hoverPM = picker.pm
            pickables.clear()

        }
        val coords = UI.viewportToScreen(x,y)
        tmp.set(coords.first,coords.second)
        val picked = picker.pick(viewport,batch,cam,x,y,pickables)

        return picked
    }

    fun reset() {
        getPick = false
        button = -1
        key = -1
        double = false
        exec = null
    }


    fun interface PickListener {
        fun pickChanged(picked:Pickable, removed:Boolean)
    }

    companion object {
        val batch : ModelBatch = ModelBatch(PickerShader)
    }

}