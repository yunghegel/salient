package org.yunghegel.salient.engine.api.dto

interface DTOAdapter<Source,Model> {

    fun fromDTO(dto: Model) : Source

    fun toDTO(model: Source): Model



}

