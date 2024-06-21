package org.yunghegel.salient.editor.plugins.gizmos.systems

import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import org.yunghegel.salient.editor.plugins.BaseSystem
import org.yunghegel.salient.editor.plugins.picking.tools.PickingTool
import org.yunghegel.salient.engine.graphics.TransformState
import org.yunghegel.salient.engine.scene3d.component.SelectedComponent
import org.yunghegel.salient.engine.scene3d.component.TransformComponent
import org.yunghegel.salient.engine.tool.PickableTool

class GizmoSystem : BaseSystem("gizmo_system",0, Family.all(TransformComponent::class.java, SelectedComponent::class.java).get()) {

    var activeGizmo : PickableTool? = null
    val picker by lazy {  tool<PickingTool>("picking_tool") }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        if (activeGizmo == null) return

    }

    fun poll() {
        val x = Gdx.input.x.toFloat()
        val y = Gdx.input.y.toFloat()
        val pickables = activeGizmo?.handles?.toList()
        if (pickables != null) {
            picker.queryFor(x, y, pickables) { id ->

                activeGizmo?.currentHandle = activeGizmo?.handles?.find { it.id == id }
            }
        }


    }

}