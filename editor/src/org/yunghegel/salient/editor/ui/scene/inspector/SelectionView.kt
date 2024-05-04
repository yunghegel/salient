package org.yunghegel.salient.editor.ui.scene.inspector

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.utils.Selection
import ktx.actors.onClick
import ktx.collections.GdxArray
import mobx.core.autorun
import org.yunghegel.gdx.utils.data.EnumBitmask
import org.yunghegel.salient.editor.app.salient
import org.yunghegel.salient.engine.events.scene.onGameObjectSelected
import org.yunghegel.salient.engine.events.scene.onSingleGameObjectSelected
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.system.inject
import org.yunghegel.salient.engine.ui.layout.Panel
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.SList
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.STextButton
import org.yunghegel.salient.engine.ui.widgets.BitmaskConfigWidget
import org.yunghegel.salient.engine.ui.widgets.BufferWindow
import org.yunghegel.salient.engine.ui.widgets.FlagsEditor

class SelectionView : ObjectInspector<GameObject>("Selection","settings") {

    var last : GameObject? = null
    val selection : Selection<GameObject> = inject()

    init {


        autorun {
            if (last != selection.lastOrNull()) {
                last = selection.lastOrNull()
                obj = last
                populateLayout(obj)
            }
        }


        background = null

        onSingleGameObjectSelected {
            obj = it
            populateLayout(obj)
        }
    }



    override fun injectObject(): GameObject? {
        return selection.lastOrNull()
    }

    override fun populateLayout(obj: GameObject?) {
        clearContents()
        add(makeIdentifierBlock(obj)).growX().pad(15f).row()
        add(makeComponentList(obj)).growX().pad(15f).row()
        add(makeFlagsBlock(obj)).growX().pad(15f).row()

        salient {
            pipeline.buffers.forEach { entry ->
                val pass = STextButton(entry.key)
                val bufferView = BufferWindow(entry.key)
                entry.value?.let { fbo ->
                    bufferView.create(fbo)
                    pass.onClick {
                        bufferView.show(0f,0f,800f,600f,stage)
                    }
                    add(pass).growX().pad(15f).row()
                }
            }
        }
    }

    fun makeIdentifierBlock(gameObject: GameObject?) : STable{
        val container = STable()
        val group = HorizontalGroup()
        group.space(15f)
        if(gameObject != null) {
            group.addActor(SLabel("Name: ${gameObject.name}"))
            group.addActor(SLabel("ID: ${gameObject.id}"))
        } else {
            group.addActor(SLabel("No Selection"))
        }
        container.add(group).pad(5f).growX()
        return container
    }

    fun makeComponentList(gameObject: GameObject?) : SList<String> {
        val list = SList<String>()
        val items = GdxArray<String>()
        if (gameObject != null) {
            gameObject.components.forEach { component ->
                items.add(component::class.java.simpleName)
            }
        } else {
            items.add("No Selection")
        }

        list.setItems(items)
        return list
    }

    fun makeFlagsBlock(gameObject: GameObject?) : STable {
        val container = Panel()
        if (gameObject!=null ){
            val editor = BitmaskConfigWidget(gameObject.bitmask,2)
            container.add(editor).growX()
            container.createTitle("Flags")
        } else {
            container.add  (SLabel("No Selection")).growX().pad(15f).row()
        }


        return container
    }

}