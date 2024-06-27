package org.yunghegel.salient.engine.scene3d.graph

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import org.yunghegel.salient.engine.graphics.Transformable

open class Spatial<T: Spatial<T>>(name:String,) : Node<T,Matrix4>(name) , Transformable {



    private val tempMat = Matrix4()
    private val tempQuat = Quaternion()

    private val localPosition: Vector3
    private val localRotation: Quaternion
    private val localScale: Vector3

    var combined: Matrix4 = Matrix4()
        private set

    init {
        localScale = Vector3(1f, 1f, 1f)
        localPosition = Vector3()
        localRotation = Quaternion()
    }

    fun getTransform(localPosition: Vector3, localRotation:Quaternion, localScale:Vector3): Matrix4 {
        if (parent == null) {
            return combined.set(localPosition, localRotation, localScale)
        } else {
            combined.set(localPosition, localRotation, localScale)
            return combined.mulLeft(parent?.getTransform())
        }
    }

    fun getTransform() : Matrix4 {
        if (parent == null) {
            return combined.set(localPosition, localRotation, localScale)
        } else {
            combined.set(localPosition, localRotation, localScale)
            return combined.mulLeft(parent?.getTransform())
        }
    }

    fun getLocalPosition(out: Vector3): Vector3 {
        return out.set(localPosition)
    }

    fun getLocalRotation(out: Quaternion): Quaternion {
        return out.set(localRotation)
    }

    fun getLocalScale(out: Vector3): Vector3 {
        return out.set(localScale)
    }

    fun setLocalPosition(position: Vector3) {
        markDirty()
        localPosition.set(position)
    }

    fun setLocalRotation(rotation: Quaternion) {
        markDirty()
        localRotation.set(rotation)
    }

    fun setLocalScale(scale: Vector3) {
        markDirty()
        localScale.set(scale)
    }

    override fun getPosition(out: Vector3): Vector3 {
        return out.set(getTransform().getTranslation(out))
    }

    override fun getRotation(out: Quaternion): Quaternion {
        return out.set(getTransform().getRotation(out))
    }

    override fun getScale(out: Vector3): Vector3 {
        return out.set(getTransform().getScale(out))
    }

    fun setPosition(position: Vector3) {
        markDirty()
        if (parent == null) {
            localPosition.set(position)
        } else {
            tempMat.set(parent!!.getTransform()).inv()
            localPosition.set(position).mul(tempMat)
        }
    }

    override fun setRotation(quat: Quaternion) {
        markDirty()
        if (parent == null) {
            localRotation.set(quat)
        } else {
            tempQuat.set(parent!!.getRotation(tempQuat)).conjugate()
            localRotation.set(quat).mul(tempQuat)
        }
    }

    override fun setScale(x: Float, y: Float, z: Float) {
        TODO("Not yet implemented")
    }

    override fun setScale(v: Vector3) {
        markDirty()
        if (parent == null) {
            localScale.set(v)
        } else {
            tempMat.set(parent!!.getTransform()).inv()
            localScale.set(v).mul(tempMat)
        }
    }

    override fun applyTransform() {
        getTransform(localPosition, localRotation, localScale)
    }

    override fun translate(x: Float, y: Float, z: Float) {
        markDirty()
        localPosition.add(x, y, z)
    }

    override fun translate(v: Vector3) {
        markDirty()
        localPosition.add(v)
    }

    override fun rotate(quat: Quaternion) {
        markDirty()
        localRotation.mulLeft(quat)
    }

    override fun scale(x: Float, y: Float, z: Float) {
        markDirty()
        localScale.scl(x, y, z)
    }

    override fun scale(v: Vector3) {
        markDirty()
        localScale.scl(v)
    }

    override fun setTranslation(x: Float, y: Float, z: Float) {
        markDirty()
        localPosition.set(x, y, z)
    }

    override fun setTranslation(v: Vector3) {
        markDirty()
        localPosition.set(v)
    }

}