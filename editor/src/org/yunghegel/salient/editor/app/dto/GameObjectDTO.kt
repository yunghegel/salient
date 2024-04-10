package org.yunghegel.salient.editor.app.dto

import com.badlogic.gdx.math.Matrix4
import kotlinx.serialization.Serializable
import org.yunghegel.salient.editor.app.dto.datatypes.Matrix4Data

@Serializable
class GameObjectDTO {
    var name: String = "unconfigured"

    var id: String = "-1"

    var uuid: String = "00000000-0000-0000-0000-000000000000"

    var flags: Int = 0

    var transform: Matrix4Data = Matrix4Data(Matrix4())

    var children: Array<GameObjectDTO> = arrayOf()
}
