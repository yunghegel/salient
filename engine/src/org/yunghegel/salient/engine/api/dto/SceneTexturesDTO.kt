package org.yunghegel.salient.engine.api.dto

class SceneTexturesDTO {

    data class TextureDTO(
        val id: String,
        val path: String,
        val type: String,
        val format: String,
        val wrapS: String,
        val wrapT: String,
        val minFilter: String,
        val magFilter: String
    )

}