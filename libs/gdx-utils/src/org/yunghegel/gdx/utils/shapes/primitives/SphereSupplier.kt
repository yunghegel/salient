package org.yunghegel.gdx.utils.shapes.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder
import org.yunghegel.gdx.utils.shapes.BuilderUtils
import org.yunghegel.gdx.utils.shapes.InstanceSupplier

class SphereSupplier @JvmOverloads constructor(
    var radius: Float,
    var divisionsU: Int,
    var divisionsV: Int,
    color: Color? = BuilderUtils.getRandomColor()
) : InstanceSupplier(color) {
    override fun createModel(): Model? {
        modelBuilder.begin()
        b = modelBuilder.part("sphere", primitiveType, attributes, mat)
        SphereShapeBuilder.build(b, radius, radius, radius, divisionsU, divisionsV)
        return modelBuilder.end()
    }
}