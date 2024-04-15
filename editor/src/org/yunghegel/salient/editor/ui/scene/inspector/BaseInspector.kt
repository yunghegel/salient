package org.yunghegel.salient.editor.ui.scene.inspector

import com.badlogic.gdx.scenes.scene2d.Actor
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.events.scene.onGameObjectSelected
import org.yunghegel.salient.engine.graphics.scene3d.GameObject
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.ui.container.Panel

abstract class BaseInspector(val title:String, val icon:String) : Panel() {

    init {
        createIcon(icon)
        createTitle(title,"")
    }

    abstract fun createLayout()





}

abstract class ComponentInspector<T,C>(val type: Class<T>,title:String,icon:String) : BaseInspector(title,icon) where T:EntityComponent<C> {

    val settings = STable()

    var current : T? = null

    var selectedGameObject : GameObject? = null

    init {
        onGameObjectSelected {  event ->
            selectedGameObject = event.go.lastOrNull()
            current = selectedGameObject?.getComponent(type)
            if (current!=null) {
                setComponent(current!!)
            }
        }


    }

    open fun createNew(gameObject: GameObject) {}

    fun detachComponent() {
        selectedGameObject?.remove(type)
        current = null
    }

    abstract fun populate(component: T?)

    fun setComponent(component: T?) {
        populate(component)
    }
}

interface AssetsAvailable<T: Asset<*>,C:EntityComponent<*>> {

    fun retrieveAvailableAssets() : List<AssetHandle>

    fun attachSelectedAsset(asset: T, gameObject: GameObject)

    fun createAssetsView(assets: List<AssetHandle>) : Actor

}