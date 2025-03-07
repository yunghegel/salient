package org.yunghegel.salient.engine.ui.widgets.value.widgets

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import ktx.actors.onChange
import ktx.actors.onTouchDown
import ktx.actors.onTouchEvent

open class LabeledFloatField(labelText: String, width: Int=50, allowNegative: Boolean=false) : LabeledTextField(labelText,width) {

    init {
        addScrollListener()
        addDragListener()
        textField.textFieldFilter = FloatOnlyFilter(allowNegative)
        touchable = Touchable.enabled
        onTouchEvent { event, x, y ->
            if (hit(x, y, false) == textField) {
               stage.setKeyboardFocus(textField)
            } else {
                stage.keyboardFocus = null
            }
        }
    }


    fun isValid(): Boolean {
        return textField.text.matches(Regex("-?\\d*\\.?\\d*"))
    }

    fun changed(action: (Float) -> Unit) {
        textField.onChange {
            if (isValid()) {
                action(float)
            }
        }
    }

    var float: Float
        get() {
            if (isValid()) {
                return java.lang.Float.parseFloat(textField.text)
            }
            return 0f
        }
        set(value) {
            textField.text = value.toString()
        }

    private fun addDragListener(){
        textField.addListener(object : DragListener() {
            override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                Gdx.input.isCursorCatched = true
            }

            override fun drag(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                super.drag(event, x, y, pointer)
                if (isValid()) {
                    val delta = Gdx.input.deltaY * 0.1f
                    textField.text = (java.lang.Float.parseFloat(textField.text) + delta).toString()
                    Gdx.input.setCursorPosition(x.toInt(), y.toInt())
                }
            }

            override fun dragStop(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                super.dragStop(event, x, y, pointer)
                Gdx.input.isCursorCatched = false
            }
        })
    }
    private fun addScrollListener(){
        textField.addListener(object : InputListener() {
            override fun scrolled(event: InputEvent?, x: Float, y: Float, amountX: Float, amountY: Float): Boolean {
                if (isValid()) {
                    textField.text = (java.lang.Float.parseFloat(textField.text) + amountY).toString()
                }
                return super.scrolled(event, x, y, amountX, amountY)
            }
        })
    }
}