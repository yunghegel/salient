package org.yunghegel.salient.editor.ui.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import ktx.actors.onChange
import ktx.actors.onEnter
import ktx.actors.onTouchEvent
import ktx.actors.setScrollFocus
import org.yunghegel.gdx.utils.ext.defaults
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.salient.editor.scene.GameObjectSelectionManager
import org.yunghegel.salient.editor.ui.scene.graph.ObjectTree
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
    val graph : ObjectTree

    val inspector : SceneInspector

    val split : SplitPaneEx
    val selection : GameObjectSelectionManager = inject()
    val inspectorHeader : InspectorHeader

    init {

        graph = ObjectTree(inject())
        graphPane = ScrollPane(graph)
        graph.apply {
            addListener(object:InputListener(){
                override fun scrolled(event: InputEvent?, x: Float, y: Float, amountX: Float, amountY: Float): Boolean {
                        graphPane.listeners.filterIsInstance<InputListener>().each { listener ->
                            println("$x $y")
                            listener.scrolled(event,x,y,amountX,amountY)
                        }
                    return false
                }
            })

        }


        graphPane.defaults()
        val graphView = SceneGraphViewer(graphPane,inject())
        graphView.expandAll.onChange {
            graph.expandAll()
        }
        graphView.collapseAll.onChange { 
            graph.root.children.each { it.collapseAll() }
        }


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
            graph.buildTree(graph.root,graph.graph.root)
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
            graph.map[gameobject]?.let { graph.selection.remove(it) }
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
            graph.map[gameobject]?.let {graph.selection.set(graph.map[gameobject])}
            inspector.inspectors.filter { it is ComponentInspector<*,*> }.forEach {
                (it as ComponentInspector<*,*>).selectedGameObject = gameobject
            }
        }
    }

}