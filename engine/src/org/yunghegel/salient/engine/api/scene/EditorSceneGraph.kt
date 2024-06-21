package org.yunghegel.salient.engine.api.scene

import org.yunghegel.salient.engine.api.UpdateRoutine
import org.yunghegel.salient.engine.scene3d.GameObject

interface EditorSceneGraph : UpdateRoutine {

    val root : GameObject

    fun addGameObject(gameObject: GameObject, parent: GameObject? = null)

    fun removeGameObject(gameObject: GameObject)

    fun find(name: String): GameObject? {
        var go : GameObject? = null
        root.recurse { if (it.name == name) { go = it; return@recurse } }
        return go
    }

    fun find(id:Int) : GameObject? {
        var go : GameObject? = null
        root.recurse { if (it.id == id) { go = it; return@recurse } }
        return go
    }







}