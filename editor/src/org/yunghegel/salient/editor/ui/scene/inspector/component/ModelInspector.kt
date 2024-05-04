package org.yunghegel.salient.editor.ui.scene.inspector.component

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import ktx.actors.onChange
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.editor.scene.Scene
import org.yunghegel.salient.editor.ui.scene.graph.ComponentNode
import org.yunghegel.salient.editor.ui.scene.graph.SceneGraphTree
import org.yunghegel.salient.editor.ui.scene.inspector.AssetsAvailable
import org.yunghegel.salient.editor.ui.scene.inspector.ComponentInspector
import org.yunghegel.salient.engine.api.asset.locateAsset
import org.yunghegel.salient.engine.api.asset.type.ModelAsset
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.events.asset.onAssetIncluded
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.scene3d.component.ModelComponent
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.scene2d.SList
import org.yunghegel.salient.engine.ui.scene2d.STextButton
import org.yunghegel.salient.engine.ui.layout.Panel

class ModelInspector : ComponentInspector<ModelComponent, Model>(ModelComponent::class.java, "Model", "model_object"),
    AssetsAvailable<ModelAsset, ModelComponent> {

        val horizontalBounds = Vector2()

    val list = SList<String>() { list, old, new ->
        val selection = list.selection.first()
        println("Selected: $selection")
        if (selectedGameObject!=null) {
            val asset = availableAssets.firstOrNull { it.name == selection }


        }
    }

    var availableAssets : List<AssetHandle> = listOf()

    val addToSelected = STextButton("Set Model")
    val clearModel = STextButton("Clear Model")
    val createFrom = STextButton("Create From")



    init {
        onAssetIncluded { _ ->
            availableAssets = retrieveAvailableAssets()
            list.clearItems()
            list.setItems(availableAssets.map { it.name }.toTypedArray())
        }
        addToSelected.onChange {
            val string = list.selected
            val asset = availableAssets.find { it.name == string }!!
            if (selectedGameObject!=null) {
                locateAsset<ModelAsset,Model>(selectedGameObject!!.scene, asset)?.let { asset ->
                    asset.useAsset(asset.value!!,selectedGameObject!!)
                    val component = ModelComponent(asset,selectedGameObject!!)
                    selectedGameObject!!.add(component)
                }
            }
        }
        createFrom.onChange {
            val string = list.selected
            val asset = availableAssets.find { it.name == string }!!

        }



        clearModel.onChange {
            selectedGameObject?.let { go ->
                val sceneTree : SceneGraphTree = inject()
                sceneTree.nodeMap[go]?.let { node ->
                    node.children.forEach {
                        it.remove()
                    }
                }
                go.get(ModelComponent::class)?.let {
                    it.usedAsset?.let { asset ->
                        asset.removeAsset(asset.value!!,go)
                    }
                }
                go.remove(ModelComponent::class.java)

            }
        }
    }

    override fun populate(component: ModelComponent?) {
        current = component
        availableAssets = retrieveAvailableAssets()
        list.clearItems()
        list.setItems(availableAssets.map { it.name }.toTypedArray())
    }

    override fun createLayout() {
        val handles = retrieveAvailableAssets()
        val scene : Scene = inject()
        val assets = scene.assets.filterIsInstance<ModelAsset>().map { it as ModelAsset }
        val assetsView = createAssetsView(handles)
        add(assetsView).growX().row()
    }

    override fun attachSelectedAsset(asset: ModelAsset,gameObject: GameObject) {
        val string = list.selected
        val model = availableAssets.find { it.name == string }
        if (model!=null) {
            gameObject.components.find { it is ModelComponent }?.let {
                gameObject.remove(ModelComponent::class.java)
            }
            val cmp = ModelComponent(asset.handle,gameObject,asset)
            gameObject.add(cmp)
            val sceneTree : SceneGraphTree = inject()
            sceneTree.nodeMap[gameObject]?.add(ComponentNode(cmp,gameObject))
        }
    }

    override fun retrieveAvailableAssets(): List<AssetHandle> {
        val scene: Scene = inject()
        return scene.retrieveAssetIndex().filter { it.type == "MODEL" }
    }

    override fun createAssetsView(assets: List<AssetHandle>): Actor {
        val panel = Panel()
        panel.createTitle("Model Assets Available","")
        panel.add(list).grow()
        val sideButtons = VerticalGroup()
        sideButtons.addActor(addToSelected)
        sideButtons.addActor(clearModel)
        sideButtons.space(5f)
        panel.add(sideButtons).fillY().padHorizontal(10f).maxWidth(50f).row()
        return panel
    }

    override fun layout() {

        super.layout()
    }


}