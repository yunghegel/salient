package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import org.yunghegel.salient.engine.api.Dirty
import org.yunghegel.salient.engine.api.DirtyListener
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.SceneContext
import org.yunghegel.salient.engine.ui.Icon

class TransformComponent(go: GameObject) : EntityComponent<Matrix4>(go.combined,go), Icon,Dirty<TransformComponent> {

    override val listeners: MutableList<DirtyListener<TransformComponent>> = mutableListOf()

    override val iconName: String = "transform_object"

    override var dirty: Boolean = false

    init {
        listeners.add {
            go.combined.set(value)
        }
    }

    fun configure(renderableComponent: RenderableComponent) {
        val instance = (renderableComponent.renderableProvider as? ModelInstance)
        instance?.transform = go.combined
    }

    override fun update(scene: EditorScene, go: GameObject, context: SceneContext) {
        value!!.set(go.getTransform())
    }

    fun translate(x: Float, y: Float, z: Float) {
        go.translate(Vector3(x, y, z))
    }

    fun translate(v: Vector3) {
        go.translate(v)
    }

    fun rotate(quat: Quaternion) {
        go.rotate(quat)
    }

    fun scale(x: Float, y: Float, z: Float) {
        go.scale(Vector3(x, y, z))
    }

    fun scale(v: Vector3) {
        go.scale(v)
    }

    fun setTranslation(x: Float, y: Float, z: Float) {
        go.setLocalPosition(Vector3(x, y, z))
    }

    fun setTranslation(v: Vector3) {
        go.setLocalPosition(v)
    }

    fun setRotation(quat: Quaternion) {
        go.setLocalRotation(quat)
    }

    fun setScale(x: Float, y: Float, z: Float) {
        go.setLocalScale(Vector3(x, y, z))
    }

    fun setScale(v: Vector3) {
        go.setLocalScale(v)
    }

    fun get() = go.combined.cpy()
}