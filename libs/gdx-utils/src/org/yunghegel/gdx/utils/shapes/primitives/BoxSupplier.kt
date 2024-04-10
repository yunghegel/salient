package org.yunghegel.gdx.utils.shapes.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder
import org.yunghegel.gdx.utils.shapes.BuilderUtils
import org.yunghegel.gdx.utils.shapes.InstanceSupplier

open class BoxSupplier @JvmOverloads constructor(
    private val width: Float,
    private val depth: Float,
    private val height: Float,
    color: Color? = BuilderUtils.getRandomColor()
) : InstanceSupplier(color) {
    override fun createModel(): Model? {
        modelBuilder.begin()
        b = modelBuilder.part("box", primitiveType, attributes, mat)
        BoxShapeBuilder.build(b, width, depth, height)
        val model = modelBuilder.end()
        //        MeshTangentSpaceGenerator.computeTangentSpace(model.meshes.get(0), mat, true, false);
        return model
    }
}
