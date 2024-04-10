package org.yunghegel.salient.editor.scene

import com.badlogic.gdx.scenes.scene2d.utils.Selection
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ktx.ashley.addComponent
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.salient.editor.app.Gui
import org.yunghegel.salient.engine.api.scene.EditorSceneGraph
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.scene.onGameObjectDeselected
import org.yunghegel.salient.engine.events.scene.onGameObjectSelected
import org.yunghegel.salient.engine.graphics.scene3d.GameObject
import org.yunghegel.salient.engine.graphics.scene3d.component.SelectedComponent
import org.yunghegel.salient.engine.graphics.scene3d.events.GameObjectAdded
import org.yunghegel.salient.engine.graphics.scene3d.events.GameObjectAddedEvent
import org.yunghegel.salient.engine.io.inject
import org.yunghegel.salient.engine.ui.UI

class SceneGraph(val scene:Scene):EditorSceneGraph {

    @Transient
    private val gameObjects = mutableListOf<GameObject>()

    @Transient
    override val root: GameObject = GameObject("root")


    init {
        onGameObjectSelected { event ->
            event.go.each { go ->
                println("selected ${go.name}")
                go.add(SelectedComponent(go))
            }
        }
        onGameObjectDeselected { event ->
            event.gameObjects.each { go ->
                println("deselected ${go.name}")
                go.remove(SelectedComponent::class.java)
            }
        }
    }

    val selection : Selection<GameObject>
        get() = inject()



    override fun addGameObject(gameObject: GameObject, parent: GameObject?) {
        if(parent == null){
            root.addChild(gameObject)
        }else{
            parent.addChild(gameObject)
        }
        post(GameObjectAddedEvent(gameObject,parent))
    }

    fun newFromRoot(name:String): GameObject {
        val go = GameObject(name)
        addGameObject(go,root)
        return go
    }

    override fun removeGameObject(gameObject: GameObject) {
        gameObject.getParent()?.removeChild(gameObject)
    }

    override fun update(delta: Float) {
        gameObjects.forEach { go -> go.update(delta) }
    }

    fun select(gameObject: GameObject) {
        selection.add(gameObject)
    }

    fun deselect(gameObject: GameObject) {
        selection.remove(gameObject)
    }

    fun clearSelection() {
        selection.clear()
    }

    fun getSelection(): List<GameObject> {
        return selection.items().toList()
    }




}