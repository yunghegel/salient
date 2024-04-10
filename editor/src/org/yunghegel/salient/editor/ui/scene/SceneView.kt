package org.yunghegel.salient.editor.ui.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import org.yunghegel.salient.editor.ui.scene.graph.SceneGraphTree
import org.yunghegel.salient.editor.ui.scene.inspector.SceneInspector
import org.yunghegel.salient.engine.io.inject
import org.yunghegel.salient.engine.ui.layout.SplitPaneEx
import org.yunghegel.salient.engine.ui.scene2d.STable

class SceneView : STable() {

    val graphPane : ScrollPane
    val graph : SceneGraphTree

    val inspectorPane : ScrollPane
    val inspector : SceneInspector

    val split : SplitPaneEx

    init {

        graph = SceneGraphTree(inject())
        graphPane = ScrollPane(graph)

        inspector = SceneInspector()
        inspectorPane = ScrollPane(inspector)

        split = SplitPaneEx(graphPane,inspectorPane,true,true)
        split.setComputation {1-((Gdx.graphics.height * 0.75f)/Gdx.graphics.height) }

        add(split).grow()
        layout()
        split.setSplitAmount( 0.4f)
    }

}