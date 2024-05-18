package org.yunghegel.salient.editor.ui.project

import com.badlogic.gdx.utils.Align
import ktx.actors.onChange
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.editor.project.ProjectManager
import org.yunghegel.salient.editor.scene.SceneManager
import org.yunghegel.salient.engine.api.undo.ActionHistory
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.STable

class ProjectControls : STable() {

    val save : SImageButton
    val undo : SImageButton
    val redo : SImageButton

    val projManager : ProjectManager = inject()
    val sceneManager : SceneManager = inject()
    val actionManager = inject<ActionHistory>()

    init {
        align(Align.right)

        save = SImageButton("Save")
        undo = SImageButton("undo")
        redo = SImageButton("redo")

        add(save).padHorizontal(3f)
        add(undo).padHorizontal(3f)
        add(redo).padHorizontal(3f)

        save.onChange {
            projManager.saveProject(projManager.currentProject!!)
            sceneManager.saveScene(sceneManager.project.currentScene!!)
        }

        undo.onChange {
            actionManager.goBack()
        }

        redo.onChange {
            actionManager.goForward()
        }

        actionManager.listen(
            undoPossible = { undo.isDisabled = !it },
            redoPossible = { redo.isDisabled = !it }
        )




    }

}