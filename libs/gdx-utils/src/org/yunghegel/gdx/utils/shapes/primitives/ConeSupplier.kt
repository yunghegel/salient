package org.yunghegel.gdx.utils.shapes.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder
import org.yunghegel.gdx.utils.shapes.BuilderUtils
import org.yunghegel.gdx.utils.shapes.InstanceSupplier

class ConeSupplier @JvmOverloads constructor(
    var width: Float,
    var height: Float,
    var depth: Float,
    var divisions: Int,
    color: Color? = BuilderUtils.getRandomColor()
) : InstanceSupplier(color) {
    override fun createModel(): Model? {
        modelBuilder.begin()
        b = modelBuilder.part("cone", primitiveType, attributes, mat)
        ConeShapeBuilder.build(b, width, height, depth, divisions)
        return modelBuilder.end()
    }
}
