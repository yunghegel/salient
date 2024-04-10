package org.yunghegel.salient.engine.scene3d.graph

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3

open class Spatial<T:Spatial<T>>(name:String) : BaseNode<T>(name) {

    private val tempMat = Matrix4()
    private val tempQuat = Quaternion()

    private val localPosition: Vector3
    private val localRotation: Quaternion
    private val localScale: Vector3

    protected val combined: Matrix4

    init {
        combined = Matrix4()
        localScale = Vector3(1f, 1f, 1f)
        localPosition = Vector3()
        localRotation = Quaternion()
    }

    fun getTransform() : Matrix4 {
        if (parent == null) {
            return combined.set(localPosition, localRotation, localScale)
        } else {
            combined.set(localPosition, localRotation, localScale)
            return combined.mulLeft(parent!!.getTransform())
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
        localPosition.set(position)
    }

    fun setLocalRotation(rotation: Quaternion) {
        localRotation.set(rotation)
    }

    fun setLocalScale(scale: Vector3) {
        localScale.set(scale)
    }

    fun getPosition(out: Vector3): Vector3 {
        return out.set(getTransform().getTranslation(out))
    }

    fun getRotation(out: Quaternion): Quaternion {
        return out.set(getTransform().getRotation(out))
    }

    fun getScale(out: Vector3): Vector3 {
        return out.set(getTransform().getScale(out))
    }

    fun setPosition(position: Vector3) {
        if (parent == null) {
            localPosition.set(position)
        } else {
            tempMat.set(parent!!.getTransform()).inv()
            localPosition.set(position).mul(tempMat)
        }
    }

    fun setRotation(rotation: Quaternion) {
        if (parent == null) {
            localRotation.set(rotation)
        } else {
            tempQuat.set(parent!!.getRotation(tempQuat)).conjugate()
            localRotation.set(rotation).mul(tempQuat)
        }
    }

    fun setScale(scale: Vector3) {
        if (parent == null) {
            localScale.set(scale)
        } else {
            tempMat.set(parent!!.getTransform()).inv()
            localScale.set(scale).mul(tempMat)
        }
    }

    fun translate(vector: Vector3) {
        localPosition.add(vector)
    }

    fun rotate(quaternion: Quaternion) {
        localRotation.mulLeft(quaternion)
    }

    fun scale(vector: Vector3) {
        localScale.scl(vector)
    }



}