package org.yunghegel.salient.editor.scene

import org.yunghegel.salient.engine.api.dto.SceneContextDTO
import org.yunghegel.salient.engine.api.dto.SceneDTO
import org.yunghegel.salient.engine.graphics.scene3d.SceneContext

fun parseContext(context: SceneContext) : SceneContextDTO {
    val dto = SceneContextDTO()
    dto.sceneEnvironment = dto.sceneEnvironment.toDTO(context)
    return dto
}