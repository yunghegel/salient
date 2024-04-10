package org.yunghegel.gdx.utils.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder

class CylinderSupplier @JvmOverloads constructor(
    private val width: Float,
    private val height: Float,
    private val depth: Float,
    private val divisions: Int,
    color: Color? = BuilderUtils.getRandomColor()
) : InstanceSupplier(color) {
    override fun createModel(): Model? {
        modelBuilder.begin()
        b = modelBuilder.part("cylinder", primitiveType, attributes, mat)
        CylinderShapeBuilder.build(b, width, height, depth, divisions)
        return modelBuilder.end()
    }
}
