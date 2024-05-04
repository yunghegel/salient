package org.yunghegel.salient.engine.events

fun <T : Any> autoemit(value :T) : Bus.AutoEmitter<T>{ return Bus.AutoEmitter(value) }