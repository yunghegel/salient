package org.yunghegel.salient.editor.ui.scene

import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.scene2d.STable

class SceneManagementView : STable() {

    val sceneManager : SceneManager = inject()

    init {

    }

}