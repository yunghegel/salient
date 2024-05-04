package org.yunghegel.salient.editor.ui.scene.inspector

import com.badlogic.gdx.scenes.scene2d.Actor
import org.yunghegel.salient.engine.api.asset.Asset
import org.yunghegel.salient.engine.api.ecs.EntityComponent
import org.yunghegel.salient.engine.api.model.AssetHandle
import org.yunghegel.salient.engine.events.scene.onGameObjectSelected
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.ui.layout.ScrollPanel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.widgets.menu.ContextMenu

abstract class BaseInspector(val title:String, val icon:String) : ScrollPanel() {

    init {
        createIcon(icon)
        createTitle(title,"")
    }

    abstract fun createLayout()





}

abstract class ObjectInspector<T>(title:String,icon:String) : BaseInspector(title,icon) {

    var obj : T? = null

    override fun createLayout() {
        obj = injectObject()
        populateLayout(obj)
    }

    abstract fun injectObject() : T?

    abstract fun populateLayout(obj: T?)

}

abstract class ComponentInspector<T,C>(val type: Class<T>,title:String,icon:String) : BaseInspector(title,icon) where T:EntityComponent<C> {

    val settings = STable()

    var current : T? = null

    var selectedGameObject : GameObject? = null
        set(value) {
            if (value != field && value != null) {
                field = value
                current = field!!.getComponent(type)
                current?.let { setComponent(it) }
            }
        }

    init {
        onGameObjectSelected {  event ->
            selectedGameObject = event.go.lastOrNull()
            current = selectedGameObject?.getComponent(type)
            if (current!=null) {
                setComponent(current!!)
            }
        }
        val contextMenu = ContextMenu()
        contextMenu.attachListener(this)
        contextMenu["Test"] = {println("test")}


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