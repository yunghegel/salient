package org.yunghegel.salient.editor.scene

import com.badlogic.gdx.scenes.scene2d.utils.Selection
import kotlinx.serialization.Transient
import mobx.core.autorun
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.salient.editor.ui.scene.graph.SceneGraphTree
import org.yunghegel.salient.engine.api.dto.SceneGraphDTO
import org.yunghegel.salient.engine.api.scene.EditorSceneGraph
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.scene.onGameObjectDeselected
import org.yunghegel.salient.engine.events.scene.onGameObjectSelected
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.component.SelectedComponent
import org.yunghegel.salient.engine.scene3d.events.GameObjectAddedEvent
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.system.singleton

class SceneGraph(val scene:Scene):EditorSceneGraph {

    @Transient
    private val gameObjects = mutableListOf<GameObject>()

    @Transient
    override var root: GameObject = GameObject("root",scene = scene)

    var sceneTree : SceneGraphTree? = null

    val selection : Selection<GameObject> = Selection<GameObject>()

    private val _selection : GameObject?
        get() = selection.first()

    init {

        autorun {
            println("Selection: ${_selection?.name}")
        }

        singleton(selection)
        selection.multiple = true

        onGameObjectSelected { event ->
            event.go.each { go ->
                go.add(SelectedComponent(go))
            }
        }
        onGameObjectDeselected { event ->
            event.gameObjects.each { go ->
                go.remove(SelectedComponent::class.java)
            }
        }
    }

    override fun addGameObject(gameObject: GameObject, parent: GameObject?) {
        if(parent == null){
            root.addChild(gameObject)
        }else{
            parent.addChild(gameObject)
        }
        post(GameObjectAddedEvent(gameObject,parent))
    }

    private fun recurseGameObject(gameObject: GameObject, action: (GameObject) -> Unit) {
        action(gameObject)
        gameObject.getChildren().forEach { recurseGameObject(it, action) }
    }

    fun traverse(action: (GameObject) -> Unit) {
        recurseGameObject(root, action)
    }

    fun newFromRoot(name:String): GameObject {
        val go = GameObject(name,scene = scene)
        addGameObject(go,root)
        scene.manager.saveScene(scene)
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

    fun selectOnly(gameObject: GameObject) {
        selection.clear()
        selection.add(gameObject)
    }

    fun deselect(gameObject: GameObject) {
        selection.remove(gameObject)
    }

    fun selectNone() {
        selection.clear()
    }

    fun getSelection(): List<GameObject> {
        return selection.items().toList()
    }


    companion object  {

        fun applyDTO(dto: SceneGraphDTO, scene:SceneGraph) {
            scene.root = GameObject.fromDTO(dto.root,scene.scene)
        }

        fun toDTO(model: SceneGraph): SceneGraphDTO {
            val dto = SceneGraphDTO()
            dto.root = GameObject.toDTO(model.root)
            return dto
        }
    }

}