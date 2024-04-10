package org.yunghegel.gdx.utils.shapes.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import org.yunghegel.gdx.utils.shapes.InstanceSupplier

class PlaneSupplier(var scale: Float, color: Color?) : InstanceSupplier(color) {
    override fun createModel(): Model? {
        modelBuilder.begin()
        b = modelBuilder.part("plane", primitiveType, attributes, mat)
        b.rect(
            -scale, -scale, 0f,
            -scale, scale, 0f,
            scale, scale, 0f,
            scale, -scale, 0f,
            0f, 0f, -1f
        )
        return modelBuilder.end()
    }
}
