package org.yunghegel.salient.engine.ui.widgets

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.scene2d.STextField

class EditableTextField(content: String, val onFinished: (String)->Unit = {}) : STable() {

    var editMode = false
        private set

    var doubleClickOnly = true

    var previous: String = content

    val textField : STextField = STextField(content,"editable")

    val stageListner = object : InputListener() {
        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
//            if (!editMode) return false
            val hit = stage.hit(x, y, false)
//
//            hit?.let {
//                if (!it.isDescendantOf(textField)){
//                    finishEditing(false)
//                } else {
//                    return true
//                }
//            }

            if (hit != null) {
                if (!hit.isDescendantOf(textField)) {
                    println("hit: false")
                    finishEditing(false)
                } else {
                    println("hit: true")
                    return true
                }
            }
            println(hit)

            return false
        }
    }



    val listener : ClickListener = object : ClickListener() {
        override fun clicked(event: com.badlogic.gdx.scenes.scene2d.InputEvent, x: Float, y: Float) {
            if (doubleClickOnly && tapCount != 2) return
            if (tapCount == 2 && editMode) {
                finishEditing(false)
                return
            }
            if(editMode) return
            setEditable()
        }

        override fun keyDown(event: InputEvent?, keycode: Int): Boolean {
            if (keycode == com.badlogic.gdx.Input.Keys.ENTER) {
                finishEditing(false)
                return true
            } else if (keycode == com.badlogic.gdx.Input.Keys.ESCAPE) {
                finishEditing(true)
                return true
            }
            return false
        }
    }

    init {
        touchable = com.badlogic.gdx.scenes.scene2d.Touchable.enabled
        align(Align.left)
        textField.isDisabled = true
        add(textField).grow().center()
        addListener(listener)


    }

    private fun setEditable() {
        stage.addListener(stageListner)
        if (editMode) {
            org.yunghegel.salient.engine.system.error("Already in edit mode")
            return
        }
//        stage.addListener(stageListner)
        textField.selectAll()
        previous = textField.text
        textField.isDisabled = false
        stage.keyboardFocus = textField
        editMode = true
    }

    private fun finishEditing(cancel : Boolean) {
        if (!editMode) {
             org.yunghegel.salient.engine.system.error("Not in edit mode")
            return
        }
        textField.isDisabled = true
        stage.keyboardFocus = null
        if (!cancel || textField.text != previous || textField.text.isEmpty()) {
            onFinished(textField.text)
        } else {
            textField.text = previous
        }
        stage.removeListener(stageListner)
        editMode = false
    }

    fun setText(text: String) = textField.setText(text)

}