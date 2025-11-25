package org.yunghegel.gdx.renderer.env

import com.badlogic.gdx.math.Vector3

class DirectionalLight : Light("directionLight") {
    val direction = Vector3(-0.2f, -1.0f, -0.3f).nor() // slightly from the left, above, and in front

    val ambient = Vector3(0.2f, 0.2f, 0.2f)  // low-intensity base light
    val diffuse = Vector3(0.5f, 0.5f, 0.5f)  // main light color/intensity
    val specular = Vector3(1.0f, 1.0f, 1.0f) //

}