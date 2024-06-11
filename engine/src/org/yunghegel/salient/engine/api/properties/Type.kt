package org.yunghegel.salient.engine.api.properties

import org.yunghegel.gdx.utils.data.Named

interface Type : Unique {

}

interface Typed<T> : Type {
    val value : Class<T>

    override val identifier: String
        get() = value.simpleName
}