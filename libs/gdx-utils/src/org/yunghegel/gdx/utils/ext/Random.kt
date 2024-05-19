package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.math.Vector2

object rand {

    fun float(min: Float, max: Float) = (min + Math.random() * (max - min)).toFloat()

    fun int(min: Int, max: Int) = (min + Math.random() * (max - min)).toInt()

    fun bool() = Math.random() > 0.5

    fun vec2(min: Float, max: Float) = Vector2(float(min, max), float(min, max))

    fun vec3(min: Float, max: Float) = vec3(float(min, max), float(min, max), float(min, max))

    fun vec3norm() = vec3(-1f, 1f)

}