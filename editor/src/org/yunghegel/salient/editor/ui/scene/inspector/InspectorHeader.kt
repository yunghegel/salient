package org.yunghegel.salient.editor.ui.scene.inspector

import ktx.actors.onChange
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.engine.events.scene.onSingleGameObjectDeselected
import org.yunghegel.salient.engine.events.scene.onSingleGameObjectSelected
import org.yunghegel.salient.engine.scene3d.GameObject
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.STextButton
import org.yunghegel.salient.engine.ui.scene2d.STextField
import org.yunghegel.salient.engine.ui.widgets.value.widgets.LabeledTextField

class InspectorHeader : STable() {

    var current : GameObject? = null

    val nameField : LabeledTextField

    val tags : STable

    init {

        pad(10f)

        nameField = LabeledTextField("Name",75)
        tags = STable()

        add(nameField).growX().padHorizontal(10f).row()
        add(tags).growX().pad(10f).row()

        createListeners()
    }

    fun set(go: GameObject) {
        current = go
        nameField.text = go.name
        tags.clearChildren()
        go.tags.forEach { tag ->
            tags.add(Tag(tag)).height(20f)
        }
    }

    fun unset() {
        current = null
        nameField.text = ""
        tags.clearChildren()
    }

    fun createListeners() {
        nameField.onChange {
            current?.name = nameField.text ?: ""
        }
        onSingleGameObjectSelected { go ->
            set(go)
        }
        onSingleGameObjectDeselected { go ->
            unset()
        }

    }

    class Tag(name: String) : STable() {
        val nameField : STextField
        val removeButton : STextButton

        init {
            nameField = STextField(name)
            removeButton = STextButton("X")

            add(nameField).growX()
            add(removeButton).growX().row()
        }
    }

}