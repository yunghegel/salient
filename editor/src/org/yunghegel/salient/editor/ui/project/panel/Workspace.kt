package org.yunghegel.salient.editor.ui.project.panel

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Align
import kotlinx.serialization.json.JsonNull.content
import ktx.actors.onChange
import mobx.collections.ObservableMutableList
import mobx.core.Observer
import org.yunghegel.gdx.utils.ui.ActorList
import org.yunghegel.gdx.utils.ui.Dialogs
import org.yunghegel.gdx.utils.ui.LabelSupplier
import org.yunghegel.salient.editor.ui.project.FileTree
import org.yunghegel.salient.engine.system.file.Paths
import org.yunghegel.salient.engine.ui.Icons
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.layout.CollapsePanel
import org.yunghegel.salient.engine.ui.layout.Panel
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SImageTextButton
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.widgets.InputTable

class Workspace(val folders: ObservableMutableList<FileHandle> = ObservableMutableList(mutableListOf(Paths.SALIENT_HOME.handle))) :
    STable() {

    val panels: ActorList = ActorList(UI.skin)

    val tmp = mutableListOf<FileHandle>()

    val container = STable()

    init {

        align(Align.topLeft)
        tmp.addAll(folders)

        val add = InputTable("").apply {
            imageTextButton("Add folder to workspace", "folder") { button ->
                Dialogs.selectFolderDialog("Select Folder")?.let { folder ->
                    result["selection"] = folder
                    button.text = folder.name()
                }
            }.growX()
            addSubmit {
                folders.add(result["selection"] as FileHandle)
            }
            row()
        }

        add(add).growX().row()

        folders.forEach { folder ->
            addFolder(folder)
        }

        val scroll = ScrollPane(container, UI.skin)


        add(scroll).growX().top().row()

        folders.subscribe(object : Observer {
            override fun onChange() {
                val dif = folders - tmp
                val add = folders - dif
                val rem = tmp - folders

                add.forEach { folder ->
                    addFolder(folder)
                }

                rem.forEach { folder ->

                }

                tmp.clear()
                tmp.addAll(folders)
                invalidate()
            }
        })
    }

    fun addFolder(folder: FileHandle) {
        if (container.children.any { it is FolderPanel && it.label == folder.name() }) return
        container.add(FolderPanel(folder)).growX().top().row()
    }

    inner class FolderPanel(val folder: FileHandle, override var label: String? = folder.path()) : Panel(),
        LabelSupplier {

        val fileTree = FileTree(folder)

        init {
            align(Align.topLeft)
            createTitle(label!!)
            add(fileTree).grow()
            val remove = STable().apply {
                val button = SImageButton("delete")
                button.onChange {
                    folders.remove(folder)
                    container.removeActor(this@FolderPanel)
                }
                add(button).right()
                align(Align.right)
            }


            createTitleActor(remove) { it.align(Align.right) }

        }

    }


}