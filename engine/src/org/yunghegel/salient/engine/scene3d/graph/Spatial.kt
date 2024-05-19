package org.yunghegel.salient.engine.scene3d.graph

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import org.yunghegel.salient.engine.api.properties.ObjectPayload

open class Spatial<T: Spatial<T>>(name:String,) : Node<T,Matrix4>(name) {



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
        markDirty()
        if (parent == null) {
            localPosition.set(position)
        } else {
            tempMat.set(parent!!.getTransform()).inv()
            localPosition.set(position).mul(tempMat)
        }
    }

    fun setRotation(rotation: Quaternion) {
        markDirty()
        if (parent == null) {
            localRotation.set(rotation)
        } else {
            tempQuat.set(parent!!.getRotation(tempQuat)).conjugate()
            localRotation.set(rotation).mul(tempQuat)
        }
    }

    fun setScale(scale: Vector3) {
        markDirty()
        if (parent == null) {
            localScale.set(scale)
        } else {
            tempMat.set(parent!!.getTransform()).inv()
            localScale.set(scale).mul(tempMat)
        }
    }

    fun translate(vector: Vector3) {
        markDirty()
        localPosition.add(vector)
    }

    fun rotate(quaternion: Quaternion) {
        markDirty()
        localRotation.mulLeft(quaternion)
    }

    fun scale(vector: Vector3) {
        markDirty()
        localScale.scl(vector)
    }

}