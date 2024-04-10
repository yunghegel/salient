package org.yunghegel.gdx.utils.shapes.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CapsuleShapeBuilder
import org.yunghegel.gdx.utils.shapes.InstanceSupplier

class CapsuleSupplier(var radius: Float, var height: Float, var divisions: Int, color: Color?) :
    InstanceSupplier(color) {
    override fun createModel(): Model? {
        modelBuilder.begin()
        b = modelBuilder.part("capsule", primitiveType, attributes, mat)
        CapsuleShapeBuilder.build(b, radius, height, divisions)
        return modelBuilder.end()
    }
}
