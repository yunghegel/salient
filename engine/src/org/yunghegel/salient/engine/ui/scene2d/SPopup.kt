package org.yunghegel.salient.engine.ui.scene2d

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type.touchDown
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table.Debug.actor
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisDialog
import com.kotcrab.vis.ui.widget.VisImageButton
import ktx.actors.onChange
import org.yunghegel.salient.engine.input.Input
import org.yunghegel.salient.engine.system.event
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.table
import org.yunghegel.salient.engine.ui.widgets.InputResult
import org.yunghegel.salient.engine.ui.widgets.InputTable
import org.yunghegel.salient.engine.ui.widgets.Result
import kotlin.math.roundToInt

class SPopup : STable(), InputResult by Result() {

    var previousKeyboardFocus: Actor? = null
    var previousScrollFocus: Actor? = null

    val title: STable = table()
    val content: InputTable = InputTable(result = this)

    val result: InputResult = this
    var modal = false
    var closed = { }
    private var submitted = { result: Result -> }

    init {
        add(title).growX().maxHeight(22f).row()
        add(content).grow().row()
        title.background = UI.drawable("tab_down", Color.WHITE)
        background = UI.drawable("panel_body_background")
    }

    fun icon(name: String) {
        val icon = SImage(name)
        title.prepend(icon).size(16f).pad(3f)
    }

    fun title(text: String) {
        title.add(SLabel(text)).growX().pad(3f)
    }

    fun content(content: InputTable.() -> Unit) {
        with(this.content) {
            content()
            addSubmit()
        }
    }

    fun closeButton(on: () -> Unit) {
        title.add(VisImageButton("toast").apply {
            onChange { on() }
        }
        ).size(16f).pad(3f).align(Align.right)

    }

    fun whenSubmit(action: (Result) -> Unit) {
        submitted = action
    }

    private val focusListener = object : FocusListener() {

        override fun keyboardFocusChanged(event: FocusEvent, actor: Actor, focused: Boolean) {
            if (!focused) focusChanged(event);
        }


        override fun scrollFocusChanged(event: FocusEvent, actor: Actor, focused: Boolean) {
            if (!focused) focusChanged(event);
        }

        fun focusChanged(event: FocusEvent) {
            val stage = getStage();
            if (modal && stage != null && stage.root.getChildren().size > 0 && stage.root.children.peek().equals(this)
            ) { // Dialog is top most actor.
                val newFocusedActor = event.relatedActor;
                if (newFocusedActor != null && !newFocusedActor.isDescendantOf(this@SPopup)) event.cancel();
            }
        }
    }

    private val ignoreTouchDown = object : InputListener() {
        override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
            event.cancel();
            return false;
        }
    };

    override fun setStage(stage: Stage?) {
        if (stage == null)
            addListener(focusListener);
        else
            removeListener(focusListener);
        super.setStage(stage);
    }

    fun moveToCenter() {
        val stage = getStage()
        if (stage != null) {
            setPosition((stage.width - getWidth()) / 2, (stage.width - getHeight()) / 2)
        }
    }

    fun show(stage: Stage, action: Action?) {
        clearActions();
        removeCaptureListener(ignoreTouchDown);

        previousKeyboardFocus = null;
        var actor = stage.getKeyboardFocus();
        if (actor != null && !actor.isDescendantOf(this)) previousKeyboardFocus = actor;

        previousScrollFocus = null;
        actor = stage.getScrollFocus();
        if (actor != null && !actor.isDescendantOf(this)) previousScrollFocus = actor;

        pack();
        stage.addActor(this);
        stage.setKeyboardFocus(this);
        stage.setScrollFocus(this);
        if (action != null) addAction(action);
    }

    fun show(stage: Stage) {
        show(stage, Actions.sequence(Actions.alpha(0f), Actions.fadeIn(0.4f, Interpolation.fade)));
        setPosition(
            ((stage.width - getWidth()) / 2).roundToInt().toFloat(),
            ((stage.height - getHeight()) / 2).roundToInt().toFloat()
        );
    }

    fun submit(name: String, action: (InputResult) -> Unit) {
        content.row()
        content.button(name) {
            action(result)
            close()
        }.center().align(Align.bottom)
    }

    fun close() {
        val stage = getStage();
        if (stage != null) {
            stage.setKeyboardFocus(previousKeyboardFocus);
            stage.setScrollFocus(previousScrollFocus);
        }
        addAction(Actions.sequence(Actions.fadeOut(0.4f, Interpolation.fade), Actions.removeActor()));
    }

    class PopupBuilder {
        private val popup = SPopup()

        fun icon(name: String) {
            popup.icon(name)
        }

        fun title(text: String) {
            popup.title(text)
        }

        fun content(content: InputTable.() -> Unit) {
            popup.content.content()
        }

        fun closeButton(on: () -> Unit) {
            popup.closeButton({ on(); popup.close() })
        }


        fun submit(name: String, action: (InputResult) -> Unit) {
            popup.submit(name, action)
        }

        fun create() = popup
    }

    inner class SubmitBuilder {
        private var actipn = { result: Result -> }

        fun submitted(buttonLabel: String, action: (InputResult) -> Unit) {
            actipn = action
            content.row()
            content.button(buttonLabel) {
                action(result)
                close()
            }.center().align(Align.bottom)
        }

    }

}

