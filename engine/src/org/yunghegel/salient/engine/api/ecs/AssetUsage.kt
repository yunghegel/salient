package org.yunghegel.salient.engine.api.ecs

import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.model.AssetHandle

interface AssetUsage<Obj:Any,T: Asset<Obj>> {

    fun locateAsset(assetHandle: AssetHandle): T?

    fun applyAsset(asset: Obj)

    fun exportState(asset: T) : Map<String,String>

    fun importState(state: Map<String,String>) : T

}