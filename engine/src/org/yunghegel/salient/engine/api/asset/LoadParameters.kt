package org.yunghegel.salient.engine.api.asset

import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager

inline fun <reified T> buildParameters(crossinline callback: (AssetManager, String, Class<*>)->Unit) : AssetLoaderParameters<T> {
    val params = AssetLoaderParameters<T>()
    params.loadedCallback = AssetLoaderParameters.LoadedCallback { manager, fileName, type -> callback(manager, fileName, type) }
    return params
}
