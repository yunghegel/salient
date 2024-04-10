package org.yunghegel.salient.editor.app.dto

import kotlinx.serialization.Serializable

interface DTOAdapter<Source,Model> {

    fun fromDTO(dto: Model) : Source

    fun toDTO(model: Source): Model



}

