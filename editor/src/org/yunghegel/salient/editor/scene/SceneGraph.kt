package org.yunghegel.salient.editor.scene

import com.badlogic.gdx.scenes.scene2d.utils.Selection
import kotlinx.serialization.Transient
import org.yunghegel.gdx.utils.ext.each
import org.yunghegel.salient.engine.api.dto.SceneGraphDTO
import org.yunghegel.salient.engine.api.scene.EditorSceneGraph
import org.yunghegel.salient.engine.events.Bus.post
import org.yunghegel.salient.engine.events.scene.onGameObjectDeselected
import org.yunghegel.salient.engine.events.scene.onGameObjectSelected
import org.yunghegel.salient.engine.graphics.scene3d.GameObject
import org.yunghegel.salient.engine.graphics.scene3d.component.SelectedComponent
import org.yunghegel.salient.engine.graphics.scene3d.events.GameObjectAddedEvent
import org.yunghegel.salient.engine.system.inject

class SceneGraph(val scene:Scene):EditorSceneGraph {

    @Transient
    private val gameObjects = mutableListOf<GameObject>()

    @Transient
    override var root: GameObject = GameObject("root",scene = scene)


    init {
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

    val selection : Selection<GameObject>
        get() = inject()

    fun initialize(sceneGraphDTO: SceneGraphDTO) {

    }

    override fun addGameObject(gameObject: GameObject, parent: GameObject?) {
        if(parent == null){
            root.addChild(gameObject)
        }else{
            parent.addChild(gameObject)
        }
        post(GameObjectAddedEvent(gameObject,parent))
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

    fun deselect(gameObject: GameObject) {
        selection.remove(gameObject)
    }

    fun clearSelection() {
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