package org.yunghegel.salient.editor.plugins.gizmos.tools



import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import net.mgsx.gltf.scene3d.attributes.PBRColorAttribute
import org.yunghegel.gdx.utils.selection.Picker
import org.yunghegel.salient.editor.plugins.gizmos.systems.GizmoSystem
import org.yunghegel.salient.editor.plugins.picking.systems.PickingSystem

import org.yunghegel.salient.engine.graphics.TransformState
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.tool.InputTool
import org.yunghegel.salient.engine.tool.PickableTool
import org.yunghegel.salient.engine.tool.ToolHandle

class TranslateTool(val system : GizmoSystem) : PickableTool("translate_tool", PickingSystem.picker) {

    val xHandle : ToolHandle
    val yHandle : ToolHandle
    val zHandle : ToolHandle
    val xyzHandle : ToolHandle

    val handles : Array<ToolHandle>

    var transformState = TransformState.None
        set(value) {
            field = value
            info("Transform state: $value")
        }

    init {
        val modelBuilder = ModelBuilder()

        val xHandleModel = modelBuilder.createArrow(
            0f,
            0f,
            0f,
            .5f,
            0f,
            0f,
            ARROW_CAP_SIZE,
            ARROW_THICKNESS,
            ARROW_DIVISIONS,
            GL20.GL_TRIANGLES,
            Material(PBRColorAttribute.createBaseColorFactor(TransformState.X.color)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
        val yHandleModel = modelBuilder.createArrow(
            0f,
            0f,
            0f,
            0f,
            .5f,
            0f,
            ARROW_CAP_SIZE,
            ARROW_THICKNESS,
            ARROW_DIVISIONS,
            GL20.GL_TRIANGLES,
            Material(PBRColorAttribute.createBaseColorFactor(TransformState.Y.color)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
        val zHandleModel = modelBuilder.createArrow(
            0f,
            0f,
            0f,
            0f,
            0f,
            .5f,
            ARROW_CAP_SIZE,
            ARROW_THICKNESS,
            ARROW_DIVISIONS,
            GL20.GL_TRIANGLES,
            Material(PBRColorAttribute.createBaseColorFactor(TransformState.Z.color)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )
        val xyzHandleModel = modelBuilder.createSphere(
            .25f / 3,
            .25f / 3,
            .25f / 3,
            20,
            20,
            Material(PBRColorAttribute.createBaseColorFactor(TransformState.XYZ.color)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal).toLong()
        )

        xHandle = ToolHandle(TransformState.X.id, xHandleModel)
        yHandle = ToolHandle(TransformState.Y.id, yHandleModel)
        zHandle = ToolHandle(TransformState.Z.id, zHandleModel)
        xyzHandle = ToolHandle(TransformState.XYZ.id, xyzHandleModel)

        handles = arrayOf(xHandle, yHandle, zHandle, xyzHandle)

        handles.forEach { handle -> addHandle(handle) }


    }

    override fun handlePick(handle: ToolHandle) {
        transformState = TransformState.fromId(handle.id)
        handle.setColor(transformState.color)
    }

    override fun handleEndPick(handle: ToolHandle) {
        transformState = TransformState.None
        handle.restoreColor()
    }


}