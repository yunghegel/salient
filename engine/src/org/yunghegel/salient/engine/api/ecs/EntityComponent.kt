package org.yunghegel.salient.engine.api.ecs

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g3d.ModelBatch
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.graphics.scene3d.GameObject
import org.yunghegel.salient.engine.graphics.scene3d.SceneContext

abstract class EntityComponent<T>(val type: Class<T>?,val value : T?,val go: GameObject) : Component {

    val flags = ComponentFlags()

    internal val assetUsage : MutableList<AssetHandle> = mutableListOf()

    open val renderer : Boolean = false

    open val updater : Boolean = false

    open fun update(scene:EditorScene, go: GameObject, context: SceneContext) {}

    open fun render(batch: ModelBatch, camera: Camera, context: SceneContext) {}

}