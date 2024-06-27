package org.yunghegel.salient.editor.plugins.gizmos.systems

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import org.yunghegel.salient.editor.app.configs.Settings.Companion.i
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.editor.plugins.gizmos.lib.Gizmo
import org.yunghegel.salient.editor.plugins.picking.tools.PickingTool
import org.yunghegel.salient.editor.scene.GameObjectSelectionManager
import org.yunghegel.salient.engine.graphics.TransformState
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.component.SelectedComponent
import org.yunghegel.salient.engine.scene3d.component.TransformComponent
import org.yunghegel.salient.engine.system.Netgraph
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.system.nio.poll
import org.yunghegel.salient.engine.tool.PickableTool

class GizmoSystem : BaseSystem("gizmo_system",0, Family.all(TransformComponent::class.java, SelectedComponent::class.java).get()) {

    var activeGizmo : Gizmo<GameObject,*>? = null
    val picker by lazy {  tool<PickingTool>("picking_tool") }
    val selection : GameObjectSelectionManager = inject()

    init {
        Netgraph.add("Gizmo:") { "active: ${activeGizmo?.name ?: "none\n ${activeGizmo?.activeHandle?.id}\n target: ${activeGizmo?.target.toString()}" } "}
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        if (activeGizmo == null) return
        else activeGizmo!!.update(deltaTime,selection.selection.lastSelected)
        poll()

    }

    fun poll() {
        val x = Gdx.input.x
        val y = Gdx.input.y
        val pickables = activeGizmo?.handles?.toList()
        if (pickables != null) {
            activeGizmo?.let { gizmo->
                gizmo.pickHandles(x,y)
            }
        }


    }

}