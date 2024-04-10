package org.yunghegel.gdx.utils.ext

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3


fun Camera.dst2(point: Vector3): Float {
    val x_d = point.x - this.position.x
    val y_d = point.y - this.position.y
    val z_d = point.z - this.position.z
    return x_d * x_d + y_d * y_d + z_d * z_d
}

fun Camera.distanceFalloff(point: Vector3, maxDistance: Float, minDistance: Float): Float {
    val dst2 = this.dst2(point)
    return if (dst2 > maxDistance * maxDistance) 0f
    else if (dst2 < minDistance * minDistance) 1f
    else 1 - (dst2 - minDistance * minDistance) / (maxDistance * maxDistance - minDistance * minDistance)
}

//example

//val camera = PerspectiveCamera(67f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
//camera.position.set(0f, 0f, 0f)
//camera.lookAt(0f, 0f, 0f)
//camera.near = 0.1f
//camera.far = 300f

//val point = Vector3(0f, 0f, 0f)
//val maxDistance = 100f
//val minDistance = 10f

//val distanceFalloff = camera.distanceFalloff(point, maxDistance, minDistance)
//println(distanceFalloff) // 1.0

//modulating transforms with this
//camera.translate(Vector3(0f, 0f, 10f * distanceFalloff))


