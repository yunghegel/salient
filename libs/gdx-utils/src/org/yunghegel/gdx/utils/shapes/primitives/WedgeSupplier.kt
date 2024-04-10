package org.yunghegel.gdx.utils.shapes.primitives

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute
import org.yunghegel.gdx.utils.shapes.BuilderUtils
import org.yunghegel.gdx.utils.shapes.InstanceSupplier
import kotlin.math.cos
import kotlin.math.sin

class WedgeSupplier(color: Color, private val radius: Float, val angle:Float) : InstanceSupplier(color) {


    constructor(radius: Float,angle:Float) : this(BuilderUtils.getRandomColor(), radius,angle)

    override fun createModel(): Model? {
        modelBuilder.begin()
        b = modelBuilder.part("wedge", primitiveType, attributes, mat)
        mat!!.set(IntAttribute(IntAttribute.CullFace, 0))

        val x = (radius * cos(angle.toDouble())).toFloat()
        val z = (radius * sin(angle.toDouble())).toFloat()

        v(-radius, radius, radius)
        v(radius, radius, radius)
        v(radius, radius, -radius)
        v(-radius, radius, -radius)

        v(radius, -radius, -radius)
        v(-radius, -radius, -radius)


        //        b.rect( i1 = 0,i2=1,i3=2,i4=3);
        b.triangle(0,1,2)
        b.triangle(0,2,3)


        b.index(0,3,5)
        b.index(4,2,1)

        //        b.rect( i1 = 1,i2=0,i3=5,i4=4);
        b.triangle(1,0,5)
        b.triangle(1,5,4)

        //        b.rect( i1 = 4,i2=5,i3=3,i4=2);
        b.triangle(4,5,3)
        b.triangle(4,3,2)


        return modelBuilder.end()
    }
}
