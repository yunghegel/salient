package org.yunghegel.salient.editor.ui.scene.graph

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import ktx.actors.onChange
import org.yunghegel.gdx.utils.ui.dialogInput
import org.yunghegel.salient.editor.scene.SceneGraph
import org.yunghegel.salient.engine.ui.scene2d.STextButton
import org.yunghegel.salient.ui.container.Panel

class SceneGraphViewer(pane: ScrollPane, val sceneGraph:SceneGraph) : Panel() {

    val createGameObj = STextButton("+","bold")

    init {
        padTop(4f)
        createTitle("Graph")
        createTitleActor(createGameObj) { cell ->
            cell.maxWidth(20f)
            cell.pad(2f)
            cell.right()
        }
        add(pane).grow().padTop(4f)

        createGameObj.onChange {
            dialogInput("Create Game Object","Enter name","New Game Object" ) {
                onFinished { name ->
                    sceneGraph.newFromRoot(name)
                }
                onCancelled {

                }
            }
        }

    }

}