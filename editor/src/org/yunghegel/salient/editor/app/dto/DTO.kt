package org.yunghegel.salient.engine.api.model.dto

interface DTO<T> {

    fun fromDTO(dto: T)

    fun toDTO(): T

}