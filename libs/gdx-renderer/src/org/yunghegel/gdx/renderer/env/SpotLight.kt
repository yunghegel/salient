package org.yunghegel.gdx.renderer.env

import com.badlogic.gdx.math.Vector3

class SpotLight : Light("u_spotLight") {

    val direction =  Vector3()

    var cutOff: Float = 12.5f
    var outerCutOff: Float = 17.5f

    var constant: Float = 1.0f
    var linear: Float = 0.09f
    var quadratic: Float = 0.032f

}