package org.yunghegel.salient.editor.ui

import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.Menu
import com.kotcrab.vis.ui.widget.MenuBar
import com.kotcrab.vis.ui.widget.MenuItem
import ktx.actors.onChange
import org.yunghegel.gdx.utils.ext.BOTTOM
import org.yunghegel.gdx.utils.ext.LEFT
import org.yunghegel.gdx.utils.ext.RIGHT
import org.yunghegel.salient.engine.api.undo.Action
import org.yunghegel.salient.engine.api.undo.ActionHistory
import org.yunghegel.salient.engine.events.history.onActionExecuted
import org.yunghegel.salient.engine.events.history.onActionUndone
import org.yunghegel.salient.engine.io.inject
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImage

class AppBar : MenuBar() {

    val files = Menu("File")
    val edit = Menu("Edit")
    val view = Menu("View")
    val help = Menu("Help")



    init {



        addMenu(files)
        addMenu(edit)
        addMenu(view)
        addMenu(help)

        createViewMenu()
        createEditMenu()
    }

    fun createEditMenu() {
        val undo = MenuItem("Undo")
        undo.align(Align.left)
        undo.labelCell.space(0f)
        undo.defaults().space(0f)
        val redo = MenuItem("Redo")
        undo.setShortcut("Ctrl + Z")
        redo.setShortcut("Ctrl + Shift + Y")



        val history : ActionHistory = inject()

        onActionExecuted { event ->
            val name =  event.action.name
            redo.isDisabled = !history.isRedoPossible()
            undo.isDisabled = false

        }

        onActionUndone { event ->
            val name =  event.action.name
            undo.isDisabled = history.pointer == 0
            redo.isDisabled = false

        }

        undo.onChange {
            println(history.pointer)
            history.goBack()
        }

        redo.onChange {
            history.goForward()
        }

        edit.addItem(undo)
        edit.addItem(redo)




    }

    fun createViewMenu() {
        val toggleConsole = MenuItem("Toggle Console")
        val toggleInspector = MenuItem("Toggle Project Panel")
        val toggleProject = MenuItem("Toggle Scene Panel")



        toggleConsole.onChange {
            UI.root.hide(BOTTOM)
        }
        toggleInspector.onChange {
            UI.root.hide(RIGHT)
        }
        toggleProject.onChange {
            UI.root.hide(LEFT)
        }

        view.addItem(toggleConsole)
        view.addItem(toggleInspector)
        view.addItem(toggleProject)

    }


}