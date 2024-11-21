package org.yunghegel.salient.engine.graphics.shapes

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Attribute
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import kotlin.math.sqrt

abstract class InstanceSupplier(protected var color: Color?) : ModelSupplier {
    protected var modelBuilder: ModelBuilder = ModelBuilder()
    lateinit var b: MeshPartBuilder
    var v0: VertexInfo = VertexInfo()
    var v1: VertexInfo = VertexInfo()
    protected var i1: Short = 0
    protected var i2: Short = 0
    protected var i3: Short = 0
    protected var i4: Short = 0

    protected var mat: Material? = BuilderUtils.buildMaterial(color)
    protected var pos: Vector3 = Vector3()
    protected var primitiveType: Int = GL20.GL_TRIANGLES
    protected var attributes: Long =
        (VertexAttributes.Usage.Position or VertexAttributes.Usage.TextureCoordinates or VertexAttributes.Usage.Normal or VertexAttributes.Usage.ColorUnpacked).toLong()



    fun setPrimitiveType(primitiveType: Int): InstanceSupplier {
        this.primitiveType = primitiveType
        return this
    }

    fun setAttributes(attributes: Long): InstanceSupplier {
        this.attributes = attributes
        return this
    }

    fun setColor(color: Color?): InstanceSupplier {
        this.color = color
        return this
    }

    fun setPosition(pos: Vector3): InstanceSupplier {
        this.pos = pos
        return this
    }

    fun setPosition(x: Float, y: Float, z: Float): InstanceSupplier {
        pos[x, y] = z
        return this
    }

    fun addMaterialAttribute(attribute: Attribute?): InstanceSupplier {
        mat!!.set(attribute)
        return this
    }

    fun addMaterialAttributes(vararg attributes: Attribute?): InstanceSupplier {
        for (attribute in attributes) {
            mat!!.set(attribute)
        }
        return this
    }

    fun build(): ModelInstance {
        return ModelInstance(createModel(), pos)
    }

    fun createVertexInfo(pos: Vector3?, normal: Vector3?, color: Color?): VertexInfo {
        return VertexInfo().setPos(pos).setNor(normal).setCol(color)
    }

    fun createVertexInfo(pos: Vector3?, normal: Vector3?): VertexInfo {
        return VertexInfo().setPos(pos).setNor(normal).setCol(color)
    }

    fun createVInfo(pos: Vector3, color: Color?): VertexInfo {
        val normal = pos.cpy().nor()
        return VertexInfo().setPos(pos).setNor(normal).setCol(color)
    }

    fun createVInfo(x: Float, y: Float, z: Float): VertexInfo {
        val normal = pos.cpy().nor()
        return VertexInfo().setPos(pos).setNor(normal)
    }


    fun v3(x: Float, y: Float, z: Float): Vector3 {
        return Vector3(x, y, z)
    }

    fun v(x: Float, y: Float, z: Float): Short {
        b.setColor(color)
        return b.vertex(v3(x, y, z), v3(x, y, z).cpy().nor(), color, null)
    }


    fun toOneDimensionalIndex(rowIndex: Int, colIndex: Int, numberOfColumns: Int): Int {
        return rowIndex * numberOfColumns + colIndex
    }


    companion object {
        const val PI: Float = Math.PI.toFloat()
        const val HALF_PI: Float = PI * 0.5f
        const val TWO_PI: Float = PI + PI
        const val ONE_THIRD: Float = 1f / 3f
        const val QUARTER_PI: Float = PI / 4f
        const val ZERO_TOLERANCE: Float = 0.00001f
        const val TRIBONACCI_CONSTANT: Float = 1.8392868f
        val GOLDEN_RATIO: Float = ((1f + sqrt(5.0)) / 2f).toFloat()
    }
}
