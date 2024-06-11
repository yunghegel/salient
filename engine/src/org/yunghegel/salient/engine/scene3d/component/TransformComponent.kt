package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion
import com.badlogic.gdx.math.Vector3
import org.yunghegel.salient.engine.api.Dirty
import org.yunghegel.salient.engine.api.DirtyListener
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.graphics.Transformable
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.ui.Icon

class TransformComponent(go: GameObject) : EntityComponent<Matrix4>(go.combined,go), Icon,Dirty<TransformComponent>, Transformable {

    override val listeners: MutableList<DirtyListener<TransformComponent>> = mutableListOf()

    override val iconDrawableName: String = "transform"

    override var dirty: Boolean = false

    override fun translate(x: Float, y: Float, z: Float) {
        go.translate(Vector3(x, y, z))
        markDirty()
    }

    override fun translate(v: Vector3) {
        go.translate(v)
        markDirty()
    }

    override fun rotate(quat: Quaternion) {
        go.rotate(quat)
        markDirty()
    }

    override fun scale(x: Float, y: Float, z: Float) {
        go.scale(Vector3(x, y, z))
        markDirty()
    }

    override fun scale(v: Vector3) {
        go.scale(v)
        markDirty()
    }

    override fun setTranslation(x: Float, y: Float, z: Float) {
        go.setLocalPosition(Vector3(x, y, z))
        markDirty()
    }

    override fun setTranslation(v: Vector3) {
        go.setLocalPosition(v)
        markDirty()
    }

    override fun setRotation(quat: Quaternion) {
        go.setLocalRotation(quat)
        markDirty()

    }

    override fun setScale(x: Float, y: Float, z: Float) {
        go.setLocalScale(Vector3(x, y, z))
        markDirty()

    }

    override fun setScale(v: Vector3) {
        go.setLocalScale(v)
        markDirty()
    }

    override fun onDirty() {
        get()
    }

     fun get() = go.getTransform()
}