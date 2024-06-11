package org.yunghegel.salient.engine.graphics

import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3

interface Transformable {

    fun translate(x: Float, y: Float, z: Float)

    fun translate(v: Vector3)

    fun rotate(quat: Quaternion)

    fun scale(x: Float, y: Float, z: Float)

    fun scale(v: Vector3)

    fun setTranslation(x: Float, y: Float, z: Float)

    fun setTranslation(v: Vector3)

    fun setRotation(quat: Quaternion)

    fun setScale(x: Float, y: Float, z: Float)

    fun setScale(v: Vector3)
}