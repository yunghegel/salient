package org.yunghegel.salient.engine.api.ecs

import org.checkerframework.checker.units.qual.A
import org.yunghegel.gdx.utils.data.ID
import org.yunghegel.salient.engine.api.StateRecovery
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.scene3d.GameObject

abstract class AssetComponent<A: Asset<T>,T:Any, E:Enum<E>>(go: GameObject, value: T?): EntityComponent<T>(value, go), StateRecovery<E> {

    override val instructions: MutableList<StateRecovery.Payload<E>> = mutableListOf()

    override val actionMap: MutableMap<E, (Map<String, String>) -> Unit> = mutableMapOf()

    abstract fun useAsset(asset: A, value: T)

    abstract fun restoreFromUsage(identifier : ID) : Result<A>

}