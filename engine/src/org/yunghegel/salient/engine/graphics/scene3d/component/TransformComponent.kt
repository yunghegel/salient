package org.yunghegel.salient.engine.scene3d.component

import com.badlogic.gdx.math.Matrix4
import org.yunghegel.salient.engine.ecs.EntityComponent
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.graph.Spatial

class TransformComponent(val transform: Matrix4, val go: GameObject) : EntityComponent<Matrix4>(Matrix4::class.java, transform) {



}