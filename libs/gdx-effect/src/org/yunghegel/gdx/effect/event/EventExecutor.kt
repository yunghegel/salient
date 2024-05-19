package org.yunghegel.gdx.effect.event

interface EventExecutor {

    suspend fun receiveAsync(event: Event)

    fun receive(event: Event)

}