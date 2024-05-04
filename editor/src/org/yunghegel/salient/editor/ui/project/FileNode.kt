package org.yunghegel.salient.editor.ui.project

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import ktx.actors.onClick
import ktx.actors.onTouchDown
import org.yunghegel.salient.engine.ui.Icons

class FileNode(val file: FileHandle, val fileTable : FileTable = FileTable(file)) : Tree.Node<FileNode, FileHandle, FileTable>(fileTable) {

    init {
        isExpanded = true
        actor.onClick {
            if (file.isDirectory) {
                isExpanded = !isExpanded
            }
        }

        actor.onTouchDown {
            tree?.getNodeAt(Gdx.input.y.toFloat())?.let {
                tree?.selection?.set(it)
            }
        }

        if (file.isDirectory) {
            icon   = Icons.folderIcon

        } else {
            icon  = when(file.extension()){

                "salient" -> Icons.projectFile
                "mat" -> Icons.materialFile
                "png" -> Icons.textureFile
                "scene" -> Icons.sceneFile
                "obj" -> Icons.modelFile
                "fbx" -> Icons.modelFile
                "gltf" -> Icons.modelFile
                "glb" -> Icons.modelFile
                else -> Icons.fileIcon
            }
        }


        actor.addListener(object:ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                tree?.getNodeAt(y)?.let {
                    tree?.selection?.set(it)
                }
                return super.touchDown(event, x, y, pointer, button)
            }

            override fun mouseMoved(event: InputEvent?, x: Float, y: Float): Boolean {
                tree?.getNodeAt(y)?.let {
                    tree?.overNode = it
                }
                return super.mouseMoved(event, x, y)
            }

            override fun isOver(): Boolean {
                tree?.overNode = this@FileNode
                return super.isOver()
            }

        })
    }

    override fun toString(): String {
        return file.name()
    }

    fun expand() {
        if (children.isEmpty) {
            file.list().forEach {
                add(FileNode(it))
            }
        }
    }

}