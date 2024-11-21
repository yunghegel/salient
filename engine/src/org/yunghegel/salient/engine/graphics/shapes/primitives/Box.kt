package org.yunghegel.salient.engine.graphics.shapes.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder
import org.yunghegel.salient.engine.graphics.shapes.BuilderUtils
import org.yunghegel.salient.engine.graphics.shapes.InstanceSupplier

open class Box @JvmOverloads constructor(
    var width: Float,
    var depth: Float,
    var height: Float,
    color: Color? = BuilderUtils.getRandomColor()
) : InstanceSupplier(color) {
    override fun createModel(): Model {
        modelBuilder.begin()
        b = modelBuilder.part("box", primitiveType, attributes, mat)
        BoxShapeBuilder.build(b, width, depth, height)
        val model = modelBuilder.end()
        //        MeshTangentSpaceGenerator.computeTangentSpace(model.meshes.get(0), mat, true, false);
        return model
    }
}
