package org.yunghegel.salient.engine.api

import org.yunghegel.salient.engine.api.scene.EditorScene
import org.yunghegel.salient.engine.sys.Filepath

interface SceneManager {

    fun createNew(name:String) : EditorScene

    fun loadScene(file: Filepath) : EditorScene

    fun initialize(scene: EditorScene)

    fun saveScene(scene: EditorScene)

}