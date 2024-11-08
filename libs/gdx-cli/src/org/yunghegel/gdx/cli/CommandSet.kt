package org.yunghegel.gdx.cli

interface CommandSet<T> {

    fun injectDependency() : T?

    val depedency : T?
        get() = injectDependency()

}