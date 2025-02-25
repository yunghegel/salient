package org.yunghegel.salient.editor.ui.project

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.OrderedSet
import ktx.actors.onClick
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.editor.app.configs.Settings.Companion.i
import org.yunghegel.salient.engine.system.nio
import org.yunghegel.salient.engine.ui.Icons
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.scene2d.SImage
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.tree.TreeActor
import org.yunghegel.salient.engine.ui.tree.TreeNode
import org.yunghegel.salient.engine.ui.tree.TreeWidget
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds

class FileSystem(_root: FileHandle) : TreeWidget<FileTreeNode, FileHandle, FileTreeNodeTable>(_root) {

    override val resolveParent: (FileHandle) -> FileHandle? = { it.parent() }

    override val resolveChildren: (FileHandle) -> List<FileHandle>? = { it.list()?.toList() }

    init {

        add(root)

    }

    override fun constructNode(obj: FileHandle): FileTreeNode {
        val node = FileTreeNode(obj)
        map[obj] = node
        return node
    }

    override fun handleSelection(items: OrderedSet<FileTreeNode>) {
        items.forEach {
            println(it.obj)
        }
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        if (visualCount != children.size) {
            visualCount = children.size
            val max = children.map { it.width }.maxOrNull() ?: 0f
            children.forEach {
                it.width = max
            }
        }
    }


}

class FileTreeNodeTable(val file: FileHandle) : TreeActor<FileHandle>(file) {

    val iconTable = STable().apply { align(Align.left) }
    val labelTable = STable().apply { align(Align.left) }
    val buttonTable = STable().apply { align(Align.right) }


    override fun buildActor(obj: FileHandle) {

        val label = SLabel(file.name())

        val icon = if (file.isDirectory) Icons.folderIcon else when (file.extension()) {
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

        val overflow = SImageButton("overflow-menu")

        add(iconTable).padHorizontal(2f)
        add(labelTable).growX().left().minWidth(100f)
        add(buttonTable).growX().right().padHorizontal(2f)

        iconTable.add(SImage(icon)).padHorizontal(2f)
        labelTable.add(label).growX().left().minWidth(100f)
        buttonTable.add(overflow).padHorizontal(2f)

    }

}

class FileTreeNode(val fileHandle: FileHandle) :
    TreeNode<FileHandle, FileTreeNodeTable>(fileHandle.name(), fileHandle, FileTreeNodeTable(fileHandle)) {

    val path: Path = fileHandle.file().toPath()

    init {
        isExpanded = true
        actor.onClick {
            if (fileHandle.isDirectory) {
                isExpanded = !isExpanded
            }
        }
    }

}