package org.yunghegel.salient.editor.ui.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import org.yunghegel.gdx.textedit.selection
import org.yunghegel.salient.editor.scene.GameObjectSelectionManager
import org.yunghegel.salient.editor.ui.scene.graph.SceneGraphTree
import org.yunghegel.salient.editor.ui.scene.graph.SceneGraphViewer
import org.yunghegel.salient.editor.ui.scene.inspector.ComponentInspector
import org.yunghegel.salient.editor.ui.scene.inspector.InspectorHeader
import org.yunghegel.salient.editor.ui.scene.inspector.SceneInspector
import org.yunghegel.salient.editor.ui.scene.inspector.SelectionView
import org.yunghegel.salient.engine.events.scene.onSceneInitialized
import org.yunghegel.salient.engine.events.scene.onSingleGameObjectDeselected
import org.yunghegel.salient.engine.events.scene.onSingleGameObjectSelected
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.system.singleton
import org.yunghegel.salient.engine.ui.layout.SplitPaneEx
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.table

class SceneView : STable() {

    val graphPane : ScrollPane
    val graph : SceneGraphTree

    val inspector : SceneInspector

    val split : SplitPaneEx
    val selection : GameObjectSelectionManager = inject()
    val inspectorHeader : InspectorHeader

    init {

        graph = SceneGraphTree(inject())
        graphPane = ScrollPane(graph)
        graphPane.setScrollingDisabled(true,false)
        val graphView = SceneGraphViewer(graphPane,inject())


        inspector = SceneInspector()
        inspectorHeader = InspectorHeader()

        val inspectorContainer = table {
            add(inspector).grow()
        }



        singleton(inspector)



        split = SplitPaneEx(graphView,inspectorContainer,true,true)
        split.setComputation {1-((Gdx.graphics.height * 0.75f)/Gdx.graphics.height) }

        add(split).grow()
        layout()
        split.setSplitAmount( 0.4f)

        onSceneInitialized { event ->
            graph.rebuild(event.scene.graph.root)
            inspector.updateAll()
        }

        onSingleGameObjectSelected { go ->
            notifySelection(go, false)
        }

        onSingleGameObjectDeselected { go ->
            notifySelection(go, true)
        }
    }

    fun notifySelection(gameobject: GameObject, removed: Boolean) {
        if (removed) {
            graph.nodeMap[gameobject]?.let { graph.selection.remove(it) }
            inspector.inspectors.filter { it is ComponentInspector<*,*> }.forEach {
                (it as ComponentInspector<*,*>).selectedGameObject = null
            }
            inspector.inspectors.find { it is SelectionView }?.let { it as SelectionView
                if (selection.selection.isEmpty) it.populateLayout(null)

            }
        } else {
            inspector.inspectors.find { it is SelectionView }?.let { it as SelectionView
                it.populateLayout(gameobject)
            }
            graph.nodeMap[gameobject]?.let {graph.selection.set(graph.nodeMap[gameobject])}
            inspector.inspectors.filter { it is ComponentInspector<*,*> }.forEach {
                (it as ComponentInspector<*,*>).selectedGameObject = gameobject
            }
        }
    }

}