package org.yunghegel.salient.engine.api.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.SceneContext

abstract class EntityComponent<T>(var value : T?,val go: GameObject) : BaseComponent() {

    val flags = ComponentFlags()

    internal val assetUsage : MutableList<AssetHandle> = mutableListOf()



    open fun update(scene:EditorScene, go: GameObject, context: SceneContext) {}

    open fun render(batch: ModelBatch, camera: Camera, context: SceneContext) {}



}