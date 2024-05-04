package org.yunghegel.gdx.meshgen.data

interface DirtySyncronized {

    fun isDirty(): Boolean

    fun setClean()

    fun setDirty()

    fun update()

}

interface TypedDirtySyncronized<T> : DirtySyncronized {}
