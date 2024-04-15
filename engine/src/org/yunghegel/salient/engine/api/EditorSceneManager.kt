package org.yunghegel.salient.engine.api

import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.system.file.Filepath

interface EditorSceneManager<T:EditorScene> {

    fun createNew(name:String) : T

    fun loadScene(file: Filepath, makeCurrent: Boolean) : T

    fun initialize(scene: T,makeCurrent:Boolean)

    fun saveScene(scene: T)

}