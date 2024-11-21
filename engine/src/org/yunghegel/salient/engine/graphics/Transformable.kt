package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3

interface Transformable {



    fun translate(x: Float, y: Float, z: Float)

    fun translate(v: Vector3) {
        translate(v.x, v.y, v.z)
    }

    fun rotate(quat: Quaternion)

    fun scale(x: Float, y: Float, z: Float)

    fun scale(v: Vector3) {
        scale(v.x, v.y, v.z)
    }

    fun setTranslation(x: Float, y: Float, z: Float)

    fun setTranslation(v: Vector3) {
        setTranslation(v.x, v.y, v.z)
    }

    fun setRotation(quat: Quaternion)

    fun setScale(x: Float, y: Float, z: Float)

    fun setScale(v: Vector3) {
        setScale(v.x, v.y, v.z)
    }

    fun getPosition(out: Vector3) : Vector3

    fun getRotation(out: Quaternion) : Quaternion

    fun getScale(out: Vector3) : Vector3

    fun applyTransform()
}