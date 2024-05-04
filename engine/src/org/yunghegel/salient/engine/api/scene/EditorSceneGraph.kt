package org.yunghegel.salient.engine.api.scene

import org.yunghegel.salient.engine.api.UpdateRoutine
import org.yunghegel.salient.engine.scene3d.GameObject

interface EditorSceneGraph : UpdateRoutine {

    val root : GameObject

    fun addGameObject(gameObject: GameObject, parent: GameObject? = null)

    fun removeGameObject(gameObject: GameObject)








}