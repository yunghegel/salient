package org.yunghegel.salient.engine.api.scene

import org.yunghegel.salient.engine.api.UpdateRoutine
import org.yunghegel.salient.engine.graphics.scene3d.GameObject

interface EditorSceneGraph : UpdateRoutine {

    val root : GameObject

    fun addGameObject(gameObject: GameObject, parent: GameObject? = null)

    fun removeGameObject(gameObject: GameObject)

    fun traverse(function: (GameObject?) -> Unit, go: GameObject? = root) {
        function(go)
        if (go?.children == null) return
        go.children.forEach { traverse(function, it) }
    }






}