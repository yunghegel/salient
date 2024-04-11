package org.yunghegel.gdx.renderer.env

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Vector3

open class Light {

    var position: Vector3 = Vector3()
    var color: Vector3 = Vector3()
    var lightInstance: ModelInstance? = null

    var vy: Float = 0f
    var vx: Float = 0f
    var vz: Float = 0f

    fun update(deltaTime: Float) {
        vy += -30f * deltaTime

        position.y += vy * deltaTime
        position.x += vx * deltaTime
        position.z += vz * deltaTime

        if (position.y < 0.1f) {
            vy *= -0.70f
            position.y = 0.1f
        }
        if (position.x < -5) {
            vx = -vx
            position.x = -5f
        }
        if (position.x > 5) {
            vx = -vx
            position.x = 5f
        }
        if (position.z < -5) {
            vz = -vz
            position.z = -5f
        }
        if (position.z > 5) {
            vz = -vz
            position.z = 5f
        }

        lightInstance!!.transform.setToTranslation(position)
    }

}