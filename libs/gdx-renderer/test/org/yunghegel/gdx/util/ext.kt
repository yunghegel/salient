package org.yunghegel.gdx.util

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.collision.BoundingBox
import org.yunghegel.gdx.renderer.util.EnumMask

fun Model.toInstance() = ModelInstance(this)

fun List<ModelInstance>.transformEach(f: (Matrix4) -> Unit) : List<ModelInstance> = map {
    it.transform.apply { f(this) }
    it
}

fun <E:Enum<E>> maskOf(vararg enums: E) : EnumMask<E> {
    val mask = EnumMask<E>()
    mask.add(*enums)
    return mask
}

fun Matrix4.randomInRadius(radius: Float) : Matrix4 {
    val x = (Math.random() * radius).toFloat()
    val y = (Math.random() * radius).toFloat()
    val z = (Math.random() * radius).toFloat()
    this.setToTranslation(x, y, z)
    return this
}

fun Matrix4.rotateRandomly() : Matrix4 {
    val quat = Vector3().setToRandomDirection().crs(Vector3().setToRandomDirection()).nor()
    val angle = (Math.random() * 360).toFloat()
    this.rotate(quat, angle)
    return this
}

fun ModelInstance.ensureInFrustum(cam: PerspectiveCamera) : Matrix4 {
    val mat4 = transform
    val bounds = calculateBoundingBox(BoundingBox())
    val radius = bounds.getDimensions(Vector3()).len() / 2
    val maxTries = 100
    var tries = 0
    while (tries < maxTries) {
        mat4.randomInRadius(radius)
        mat4.getTranslation(bounds.getCenter(Vector3()))
        if (cam.frustum.boundsInFrustum(bounds)) {
            return mat4
        }
        tries++
    }
    return mat4
}

fun List<ModelInstance>.transformEachEnsureNoOverlap(f: (Matrix4) -> Unit) : List<ModelInstance> {
    toMutableList()
    val bounds = map { it.calculateBoundingBox(BoundingBox()) }.toMutableList()
    val instances = transformEach { mat4 ->
        f(mat4)
        bounds.forEach { box ->
            if (box.contains(mat4.getTranslation(Vector3()))) {
                mat4.ensureNoOverlap(box)
            }
        }
    }
    return instances
}

fun Matrix4.ensureNoOverlap(box: BoundingBox) {
    val mat4 = this
    val radius = box.getDimensions(Vector3()).len() / 2
    val maxTries = 100
    var tries = 0
    while (tries < maxTries) {
        mat4.randomInRadius(radius)
        mat4.getTranslation(box.getCenter(Vector3()))
        tries++
    }
}



fun ApplicationAdapter.launch(configure : ((Lwjgl3ApplicationConfiguration)->Unit)? = null) {
    val cfg = Lwjgl3ApplicationConfiguration()
    cfg.setTitle(javaClass.simpleName)
    cfg.setWindowedMode(800, 600)
    cfg.setBackBufferConfig(8, 8, 8, 8, 32, 0, 4)
    configure?.invoke(cfg)
    Lwjgl3Application(this, cfg)
}

fun Vector3.setFromSphericalCoords(radius: Float, phi: Float, theta: Float): Vector3 {
    val sinPhiRadius = Math.sin(phi.toDouble()) * radius
    x = (sinPhiRadius * Math.sin(theta.toDouble()).toFloat()).toFloat()
    y = (Math.cos(phi.toDouble()) * radius).toFloat()
    z = (sinPhiRadius * Math.cos(theta.toDouble()).toFloat()).toFloat()
    return this
}