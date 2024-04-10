package org.yunghegel.salient.editor.app.dto.datatypes

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.PerspectiveCamera
import kotlinx.serialization.Serializable
import org.yunghegel.salient.editor.app.dto.DTOAdapter

@Serializable
data class CameraData(
    val view: Matrix4Data = Matrix4Data.identity,
    val projection: Matrix4Data = Matrix4Data.identity,
    val near: Float = 0.1f,
    val far: Float = 300f,
    val aspect: Float = 1f,
    val position: Vector3Data = Vector3Data.zero,
    val rotation: Vector3Data = Vector3Data.zero
) {

    companion object : DTOAdapter<Camera,CameraData> {

        override fun fromDTO(dto: CameraData): Camera {
            return toCamera(dto)
        }

        override fun toDTO(model: Camera): CameraData {
            return fromCamera(model)
        }

        fun applyToCamera(cam: Camera, data: CameraData) {
            cam.view.set(Matrix4Data.toMat4(data.view))
            cam.projection.set(Matrix4Data.toMat4(data.projection))
            cam.near = data.near
            cam.far = data.far
            cam.viewportWidth = data.aspect
            cam.viewportHeight = 1f
            cam.position.set(data.position.x, data.position.y, data.position.z)
            cam.direction.set(data.rotation.x, data.rotation.y, data.rotation.z)
            cam.update()
        }

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
