package org.yunghegel.salient.editor.ui.project

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.scenes.scene2d.ui.Tree
import org.yunghegel.salient.editor.app.configs.Settings.Companion.i
import org.yunghegel.salient.engine.system.nio
import org.yunghegel.salient.engine.ui.UI
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds

class FileSystem (_root:FileHandle) : Tree<FileNode, FileHandle>(UI.skin) {

    var nodeMap : MutableMap<FileHandle,FileNode> = mutableMapOf()

    var _root: FileHandle = _root
        set(value) {
            field = value
            rootNode = FileNode(value)
        }

    var rootNode : FileNode = FileNode(_root)
        set(value)  {
            field = value
            clear()
        }


    init {
       rootNode = FileNode(_root)
        rootNode.expand()
        recursiveBuildTree(_root)
    }

    fun recurse (node: FileNode, action: (FileNode) -> Unit) {
        action(node)
        node.children.forEach {
            recurse(it, action)
        }
    }

    fun locate (path: Path) : FileNode? {
        var filenode : FileNode? = null
        recurse(rootNode) { node ->
            if (node.path == path) {
                filenode = node
                return@recurse
            }
        }
        return filenode
    }

    fun recursiveBuildTree(root:FileHandle, parent:FileNode? = null) {
        val rootNode = FileNode(root)
        nodeMap[root] = rootNode

        if (parent==null) add(rootNode)
        else parent.add(rootNode)

        root.list().forEach {
            if (it.isDirectory) {
                recursiveBuildTree(it, rootNode)
            } else {
                val node = FileNode(it)
                nodeMap[it] = node
                rootNode.add(node)
            }
        }
    }

}