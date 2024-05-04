package org.yunghegel.gdx.meshgen.util

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3

infix fun Vector3.screenspace(camera: Camera) = camera.project(this)

fun Vector3.sum() : Float {
    return x + y + z
}