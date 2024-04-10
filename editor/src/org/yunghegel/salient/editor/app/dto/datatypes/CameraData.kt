package org.yunghegel.salient.modules.scene.common.data

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.PerspectiveCamera
import kotlinx.serialization.Serializable
import org.yunghegel.salient.core.util.Matrix4Data
import org.yunghegel.salient.core.util.Vector3Data

@Serializable
data class CameraData(
    val view: Matrix4Data,
    val projection: Matrix4Data,
    val near: Float,
    val far: Float,
    val aspect: Float,
    val position: Vector3Data,
    val rotation: Vector3Data
) {

    companion object {

        fun fromCamera(cam: Camera): CameraData {
            return CameraData(
                Matrix4Data.fromMat4(cam.view),
                Matrix4Data.fromMat4(cam.projection),
                cam.near,
                cam.far,
                cam.viewportWidth / cam.viewportHeight,
                Vector3Data(cam.position.x, cam.position.y, cam.position.z),
                Vector3Data(cam.direction.x, cam.direction.y, cam.direction.z)
            )
        }

        fun toCamera(cam: CameraData): Camera {
            var camera = PerspectiveCamera()
            camera.view.set(Matrix4Data.toMat4(cam.view))
            camera.projection.set(Matrix4Data.toMat4(cam.projection))
            camera.near = 0.1f
            camera.far = 100f
            camera.viewportWidth = cam.aspect
            camera.viewportHeight = 1f
            camera.position.set(cam.position.x, cam.position.y, cam.position.z)
            camera.direction.set(cam.rotation.x, cam.rotation.y, cam.rotation.z)
            camera.update()
            return camera
        }
    }
}
