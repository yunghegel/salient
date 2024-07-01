package org.yunghegel.salient.editor.plugins.gizmos.systems

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.editor.plugins.gizmos.lib.Gizmo
import org.yunghegel.salient.editor.plugins.picking.PickablesBag
import org.yunghegel.salient.editor.plugins.picking.systems.PickingSystem
import org.yunghegel.salient.editor.plugins.picking.tools.PickingTool
import org.yunghegel.salient.editor.scene.GameObjectSelectionManager
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.component.SelectedComponent
import org.yunghegel.salient.engine.scene3d.component.TransformComponent
import org.yunghegel.salient.engine.system.Netgraph
import org.yunghegel.salient.engine.system.inject

class GizmoSystem : BaseSystem("gizmo_system",0, Family.one(TransformComponent::class.java, SelectedComponent::class.java,PickablesBag::class.java).get()) {

    var activeGizmo : Gizmo<GameObject,*>? = null
    val picker by lazy {  tool<PickingTool>("picking_tool") ?: PickingTool(pickingSystem = PickingSystem()) }
    val selectionManager : GameObjectSelectionManager = inject()

    init {
        Netgraph.add("Gizmo:") { "active: ${activeGizmo?.name} \nnone ${activeGizmo?.activeHandle?.id}\n target: ${activeGizmo?.target.toString()}" }
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        activeGizmo?.let { gizmo ->
            if (selectionManager.selection.lastSelected != null) {
                gizmo.update(deltaTime, selectionManager.selection.lastSelected!!)
            }
        }
        poll()

    }

    fun poll() {
        val x = Gdx.input.x
        val y = Gdx.input.y
        val pickables = activeGizmo?.handles?.toList()
        if (pickables != null) {
//            val picked = picker.queryFor(x.toFloat(), y.toFloat(), pickables) { println(it) }
//            activeGizmo?.pickHandles(x,y)
        }


    }

}