package org.yunghegel.salient.editor.plugins.gizmos.tools



import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import org.yunghegel.salient.editor.plugins.gizmos.lib.transform.TransformGizmo
import org.yunghegel.salient.editor.plugins.gizmos.systems.GizmoSystem
import org.yunghegel.salient.engine.graphics.TransformState
import org.yunghegel.salient.engine.helpers.TextRenderer.camera
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.scene3d.GameObject


class TranslateTool(system : GizmoSystem) : TransformGizmo<GameObject, TranslateTool.TranslateHandle>(system,"translate_tool",
    Input.Keys.T) {







    private val lastPos = Vector3()
    private val temp0 = Vector3()
    private val temp1 = Vector3()
    private val tempMat0 = Matrix4()




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
            Material(ColorAttribute.createDiffuse(TransformState.X.color)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.ColorUnpacked).toLong()
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
            Material(ColorAttribute.createDiffuse(TransformState.Y.color)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.ColorUnpacked).toLong()
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
            Material(ColorAttribute.createDiffuse(TransformState.Z.color)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.ColorUnpacked).toLong()
        )
        val xyzHandleModel = modelBuilder.createSphere(
            .25f / 3,
            .25f / 3,
            .25f / 3,
            20,
            20,
            Material(ColorAttribute.createDiffuse(TransformState.XYZ.color)),
            (VertexAttributes.Usage.Position or VertexAttributes.Usage.ColorUnpacked).toLong()
        )

        val xHandle = TranslateHandle(TransformState.X.id, xHandleModel)
        val yHandle = TranslateHandle(TransformState.Y.id, yHandleModel)
        val zHandle = TranslateHandle(TransformState.Z.id, zHandleModel)
        val xyzHandle = TranslateHandle(TransformState.XYZ.id, xyzHandleModel)

        handles.addAll(listOf(xHandle, yHandle, zHandle, xyzHandle))

        handles.forEach { handle ->
            handle.setColor(TransformState.fromId(handle.id).color)
        }

        renderMask.set(RenderUsage.MODEL_BATCH,true)


    }

    class TranslateHandle(id:Int,model:Model) : TransfomGizmoHandle<GameObject,TranslateHandle>(model,id) {

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
            batch.render(instance)
        }
    }


    companion object {

        const val ARROW_THICKNESS: Float = 0.3f

        const val ARROW_CAP_SIZE: Float = 0.1f

        const val ARROW_DIVISIONS: Int = 12
    }

    override fun update(delta: Float, target: GameObject) {
        if (target != null) {
            for (handle in handles) {
                handle.update(delta, target)
            }
        }
        if (state == TransformAxis.NONE) {
            return
        }


            val ray = camera.getPickRay(Gdx.input.x.toFloat(), Gdx.input.y.toFloat())
            var rayEnd = Vector3()
            target.getLocalPosition(rayEnd)
            val dst = camera.position.dst(rayEnd)
            rayEnd = ray.getEndPoint(rayEnd, dst)

            if (initTransform) {
                initTransform = false
                lastPos.set(rayEnd)
            }

            var modified = false
            val vec = Vector3()
            if (state == TransformAxis.X) {
                vec[rayEnd.x - lastPos.x, 0f] = 0f
                modified = true
            } else if (state == TransformAxis.Y) {
                vec[0f, rayEnd.y - lastPos.y] = 0f
                modified = true
            } else if (state == TransformAxis.Z) {
                vec[0f, 0f] = rayEnd.z - lastPos.z
                modified = true
            }

            if (!modified) {
                return
            }


            target.translate(vec)
            target.getTransform()
            lastPos.set(rayEnd)

    }

    override fun render(delta: Float, batch: ModelBatch) {
        renderHandles(batch)
    }

    override fun render(modelBatch: ModelBatch, environment: Environment?) {
        renderHandles(modelBatch)
    }


}