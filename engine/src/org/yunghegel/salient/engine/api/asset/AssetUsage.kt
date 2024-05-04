package org.yunghegel.salient.engine.api.asset

import org.yunghegel.salient.engine.scene3d.GameObject

interface AssetUsage<T> {

    fun useAsset(asset: T, go: GameObject)

    fun removeAsset(asset: T, go: GameObject)

}