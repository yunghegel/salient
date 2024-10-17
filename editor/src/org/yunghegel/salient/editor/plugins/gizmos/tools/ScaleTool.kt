package org.yunghegel.salient.editor.plugins.gizmos.tools

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import org.yunghegel.gdx.utils.ext.delta
import org.yunghegel.gdx.utils.selection.PickerColorEncoder
import org.yunghegel.salient.editor.plugins.gizmos.lib.GizmoHandle
import org.yunghegel.salient.editor.plugins.gizmos.lib.GizmoModels
import org.yunghegel.salient.editor.plugins.gizmos.lib.transform.TransformGizmo
import org.yunghegel.salient.editor.plugins.gizmos.systems.GizmoSystem
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.scene3d.GameObject


class ScaleTool(system : GizmoSystem) : TransformGizmo<GameObject, ScaleTool.ScaleHandle>(system,"scale_tool", Input.Keys.H) {


    private val lastPos = Vector3()
    private val temp0 = Vector3()
    private val temp1 = Vector3()
    private val tempMat0 = Matrix4()

    init {
        val x = GizmoModels.createArrowStub(Material(ColorAttribute.createDiffuse(COLOR_X)), Vector3.Zero, Vector3(0.5f, 0f, 0f));
        val y = GizmoModels.createArrowStub(Material(ColorAttribute.createDiffuse(COLOR_Y)), Vector3.Zero, Vector3(0f, 0.5f, 0f));
        val z = GizmoModels.createArrowStub(Material(ColorAttribute.createDiffuse(COLOR_Z)), Vector3.Zero, Vector3(0f, 0f, 0.5f));
        val mb = ModelBuilder()
        val xyzPlaneHandleModel: Model = mb.createBox(
            0.08f,
            0.08f,
            0.08f,
            Material(
                ColorAttribute.createDiffuse(
                    COLOR_XYZ
                )
            ),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.Normal or VertexAttributes.Usage.TextureCoordinates).toLong()
        )

        handles.add(ScaleHandle(x, TransformAxis.X.id))
        handles.add(ScaleHandle(y, TransformAxis.Y.id))
        handles.add(ScaleHandle(z, TransformAxis.Z.id))
        handles.add(ScaleHandle(xyzPlaneHandleModel, TransformAxis.XYZ.id))

        handles.forEach { h ->
            h.instance.materials.get(0).set(PickerColorEncoder.encodeRaypickColorId(h.id))
        }

        renderMask.set(RenderUsage.MODEL_BATCH,true)
    }

    override fun update(delta: Float, target: GameObject) {

            for (handle in handles) {
                handle.update(delta, target)

        }
        if (state == TransformAxis.NONE) {
            return
        }

            val ray = camera.getPickRay(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            var rayEnd: Vector3? = Vector3()
            target.getLocalPosition(rayEnd!!)
            val dst = camera.position.dst(rayEnd)
            rayEnd = ray.getEndPoint(rayEnd, dst)

            if (initTransform) {
                initTransform = false;
                lastPos.set(rayEnd);
            }
            var modified = false
            val vec = Vector3(0f, 0f, 0f);

            if (state == TransformAxis.X) {
                vec[1 + rayEnd.x - lastPos.x, 1f] = 1f
                handles.get(0).tmpScale.add(rayEnd.x - lastPos.x, 0f, 0f)
                modified = true
            } else if (state == TransformAxis.Y) {
                vec[1f, 1 + rayEnd.y - lastPos.y] = 1f
                modified = true
            } else if (state == TransformAxis.Z) {
                vec[1f, 1f] = 1 + rayEnd.z - lastPos.z
                modified = true
            } else if (state == TransformAxis.XYZ) {
                vec[1 + rayEnd.x - lastPos.x, 1 + rayEnd.y - lastPos.y] = 1 + rayEnd.z - lastPos.z
                val avg = (vec.x + vec.y + vec.z) / 3
                vec[avg, avg] = avg
                modified = true
            }


            //ensure non zero scale
            if (vec.x == 0f) vec.x = 1f
            if (vec.y == 0f) vec.y = 1f
            if (vec.z == 0f) vec.z = 1f

            target.scale(vec)
            target.applyTransform()
            lastPos.set(rayEnd)



    }

    override fun render(delta: Float, batch: ModelBatch) {

    }

    override fun render(modelBatch: ModelBatch, environment: Environment?) {
        renderHandles(modelBatch)
    }

    inner class ScaleHandle(model: Model, id: Int) : TransfomGizmoHandle<GameObject,ScaleHandle>(model, id) {


        override fun update(delta: Float, target: GameObject?) {
            target?.let { go ->
                target.getPosition(position)
                val dst: Float = camera.position.dst(position)
                val scl = dst * scaleFactor
                val sclamnt: Vector3 = Vector3(scl, scl, scl).add(tmpScale)
                scale.set(sclamnt)
                instance.transform.setToTranslationAndScaling(position, scale)
            }
        }

        override fun renderPick(batch: ModelBatch) {
            applyTransforms()
           batch.render(instance)
        }


    }


}

abstract class TransfomGizmoHandle<T, U>(model: Model, id: Int) : GizmoHandle<T>(model, id) {

    override fun render(batch: ModelBatch) {
        batch.render(instance)
    }

    fun applyTransforms() {
        instance.transform.set(position, rotation, scale)
    }

}

