package org.yunghegel.salient.editor.app.configs.camera

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import kotlinx.serialization.Serializable
import org.yunghegel.salient.engine.api.dto.datatypes.Matrix4Data
import org.yunghegel.salient.engine.api.dto.datatypes.Vector3Data
import org.yunghegel.salient.engine.helpers.Matrix4f
import org.yunghegel.salient.engine.helpers.Vector3f

@Serializable
data class CameraState(
    var view: Matrix4f = Matrix4f(),
    var projection: Matrix4f = Matrix4f(),
    var near: Float = 0.1f,
    var far: Float = 300f,
    var aspect: Float = 1.7f,
    var position: Vector3f = Vector3f(),
    var rotation: Vector3f = Vector3f()
)