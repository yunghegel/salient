package org.yunghegel.gdx.meshgen.util

import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo
import com.badlogic.gdx.math.Vector3

class VInfo : MeshPartBuilder.VertexInfo() {

    var tangent: Vector3 = Vector3()
        private set
    var binormal: Vector3 = Vector3()
        private set

    var hasTangent = false
    var hasBinormal = false

    fun setTangent(tangent: Vector3) : VInfo {
        hasTangent = true
        this.tangent.set(tangent)
        return this
    }

    fun setBinormal(binormal: Vector3) : VInfo {
        hasBinormal = true
        this.binormal.set(binormal)
        return this
    }

    fun setTangent(x: Float, y: Float, z: Float) : VInfo {
        hasTangent = true
        this.tangent.set(x,y,z)
        return this
    }

    fun setBinormal(x: Float, y: Float, z: Float) : VInfo {
        hasBinormal = true
        this.binormal.set(x,y,z)
        return this
    }

    override fun reset() {
        super.reset()
        tangent.set(0f,0f,0f)
        binormal.set(0f,0f,0f)
    }

    override fun set(other: MeshPartBuilder.VertexInfo): VInfo {
        super.set(other)
        if (other is VInfo) {
            tangent.set(other.tangent)
            binormal.set(other.binormal)
        }
        return this
    }

    override fun lerp(target: VertexInfo, alpha: Float): VInfo {
        super.lerp(target, alpha)
        if (target is VInfo) {
            tangent.lerp(target.tangent,alpha)
            binormal.lerp(target.binormal,alpha)
        }
        return this
    }

    override fun toString(): String {
        return "VInfo(position=$position, normal=$normal, tangent=$tangent, binormal=$binormal, uv=$uv)"
    }


    fun getMask() : VertexAttributes {
        val long = 0L

        val list = mutableListOf<VertexAttribute>()

        if (hasPosition) {
            list.add(VertexAttribute.Position())
        }
        if (hasNormal) {
            list.add(VertexAttribute.Normal())
        }
        if (hasTangent) {
            list.add(VertexAttribute.Tangent())
        }
        if (hasBinormal) {
            list.add(VertexAttribute.Binormal())
        }
        if (hasColor) {
            list.add(VertexAttribute.ColorUnpacked())
        }
        if (hasUV) {
            list.add(VertexAttribute.TexCoords(0))
        }

        val mask = VertexAttributes(*list.toTypedArray())

        return mask
    }



}