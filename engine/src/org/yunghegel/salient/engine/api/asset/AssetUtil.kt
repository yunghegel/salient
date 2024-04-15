package org.yunghegel.salient.engine.api.asset

import org.checkerframework.checker.units.qual.A
import org.yunghegel.salient.engine.api.asset.type.MaterialAsset
import org.yunghegel.salient.engine.api.asset.type.ModelAsset
import org.yunghegel.salient.engine.api.asset.type.ShaderAsset
import org.yunghegel.salient.engine.api.asset.type.TextureAsset
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.system.warn

inline fun <reified A:Asset<T>,T> locateAsset(scene: EditorScene, handle: AssetHandle): A? {
    val types = scene.assets.filterIsInstance<A>()
    val res =  types.firstOrNull { it.handle.uuid == handle.uuid } as A?
    if (res == null) {
        warn("Asset not found: available assets: ${types.map { it.handle.uuid }}")
    }
    return res
}

fun Iterable<Asset<*>>.findUUID(uuid: String): Asset<*>? {
    return this.firstOrNull { it.handle.uuid == uuid }
}

fun Iterable<Asset<*>>.findID(id: Int): Asset<*>? {
    return this.firstOrNull { it.handle.id == id }
}

val Iterable<Asset<*>>.models :List<ModelAsset> get() = this.filterIsInstance<ModelAsset>()
val Iterable<Asset<*>>.textures :List<TextureAsset> get() = this.filterIsInstance<TextureAsset>()
val Iterable<Asset<*>>.shaders :List<ShaderAsset> get() = this.filterIsInstance<ShaderAsset>()
val Iterable<Asset<*>>.materials :List<MaterialAsset> get() = this.filterIsInstance<MaterialAsset>()

fun AssetHandle.locateAsset(scene: EditorScene): Asset<*>? {
    val assets = scene.assets
    return assets.find { it.handle.uuid == this.uuid }
}