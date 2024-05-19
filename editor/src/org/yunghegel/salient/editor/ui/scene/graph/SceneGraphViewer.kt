package org.yunghegel.salient.editor.ui.scene.graph

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import ktx.actors.onChange
import ktx.scene2d.tooltip
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.gdx.utils.ui.dialogInput
import org.yunghegel.salient.editor.scene.SceneGraph
import org.yunghegel.salient.engine.ui.scene2d.STextButton
import org.yunghegel.salient.engine.ui.layout.Panel
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.table

class SceneGraphViewer(pane: ScrollPane, val sceneGraph:SceneGraph) : Panel() {

    val createGameObj = SImageButton("new")
    val expandAll = SImageButton("expand_all")
    val collapseAll = SImageButton("collapse_all")

    init {
        padTop(2f)
        createTitle("Graph")

        createGameObj.tooltip("scroll") {
            add(SLabel("Create Game Object","default-small"))
        }
        expandAll.tooltip("scroll") {
            add(SLabel("Expand All","default-small")).pad(4f)
        }
        collapseAll.tooltip("scroll") {
            add(SLabel("Collapse All","default-small"))
        }

        val expandCollapse = table {
            add(expandAll).padHorizontal(3f)
            add(collapseAll).padHorizontal(3f)
        }

        createTitleActor(expandCollapse) { cell ->
            cell.maxHeight(20f)
            cell.pad(1f)
            cell.right()
        }

        createTitleActor(createGameObj) { cell ->
            cell.maxWidth(20f)
            cell.pad(2f)
            cell.right()
        }

        add(pane).grow()

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