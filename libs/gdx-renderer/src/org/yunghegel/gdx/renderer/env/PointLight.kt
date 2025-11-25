package org.yunghegel.gdx.renderer.env

import com.badlogic.gdx.math.Vector3

class PointLight {

    val position: Vector3 = Vector3()


    var constant: Float = 1.0f
    var linear: Float = 0.09f
    var quadratic: Float = 0.032f

    val color: Vector3 = Vector3(1f, 1f, 1f)



}