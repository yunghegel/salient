package org.yunghegel.salient.engine.api.scene

import org.yunghegel.salient.engine.api.SceneManager
import org.yunghegel.salient.engine.api.model.SceneHandle

abstract class IScene(val ref: SceneHandle, val sceneManager: SceneManager) {

    abstract val renderer: ISceneRenderer

    abstract val sceneGraph: ISceneGraph


}