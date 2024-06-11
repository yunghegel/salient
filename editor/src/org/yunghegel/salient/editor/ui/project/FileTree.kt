package org.yunghegel.salient.editor.ui.project

import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut
import com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.OrderedSet
import ktx.actors.onClick
import org.yunghegel.gdx.utils.ext.alpha
import org.yunghegel.gdx.utils.ext.name
import org.yunghegel.gdx.utils.ext.notnull
import org.yunghegel.gdx.utils.ext.padHorizontal
import org.yunghegel.salient.editor.app.Gui
import org.yunghegel.salient.engine.onInterfaceInitialized
import org.yunghegel.salient.engine.system.debug
import org.yunghegel.salient.engine.system.file.FileOpener
import org.yunghegel.salient.engine.system.info
import org.yunghegel.salient.engine.ui.UI
import org.yunghegel.salient.engine.ui.child
import org.yunghegel.salient.engine.ui.pop
import org.yunghegel.salient.engine.ui.scene2d.SImage
import org.yunghegel.salient.engine.ui.scene2d.SImageButton
import org.yunghegel.salient.engine.ui.scene2d.SLabel
import org.yunghegel.salient.engine.ui.scene2d.STable
import org.yunghegel.salient.engine.ui.table
import org.yunghegel.salient.engine.ui.tree.TreeActor
import org.yunghegel.salient.engine.ui.tree.TreeNode
import org.yunghegel.salient.engine.ui.tree.TreeWidget
import kotlin.math.max
import kotlin.math.min

private typealias FNode = TreeNode<FileHandle, FileTree.FileTable>

@Suppress("UNCHECKED_CAST")
class FileTree(val rootObj: FileHandle) : TreeWidget<TreeNode<FileHandle, FileTree.FileTable>,FileHandle, FileTree.FileTable>(rootObj) {
    
    
    init {
        createDragAndDrop()
        style.over = null
        buildTree(root,rootObj)
    }

    private var state = object {
        var dragged : FNode? = null
        var draggedInitial : Pair<FNode,Int>? = null
        var hovered : FNode? = null
        var hoveredInitial : Pair<FNode,Int>? = null

    }

    var dragging = false

    override fun constructNode(obj: FileHandle): TreeNode<FileHandle,FileTable> {
        val node = FileNode(obj)
        node.actor.buildActor(obj)
        return node
    }
        
    override val resolveParent: (FileHandle) -> FileHandle? = { file ->
        file.parent()
    }
    override val resolveChildren: (FileHandle) -> List<FileHandle>?
        get() = { file ->
            file.list().toList()
        }

    fun createDragAndDrop() {
        dnd.addSource(object : DragAndDrop.Source(this) {
            override fun dragStart(event: InputEvent?, x: Float, y: Float, pointer: Int): DragAndDrop.Payload {
                dragging = true
                val payload = DragAndDrop.Payload()
                val node = getNodeAt(y)
                node?.let { over ->
                    over.hovered = true
                    state.dragged = over
                    tmpParent = over.parent
                    tmp.set(over.actor.x, over.actor.y)

                    payload.dragActor =table {
                        setBackground("button-rounded-edge-blue")
                        add(SImageButton(over.actor.icon!!)).padHorizontal(4f)
                        add(SLabel(over.obj.name()))
                    }
                    dragActor = over.actor
                    payload.`object` = over
                }
                return payload
            }
            override fun dragStop (event: InputEvent?, x: Float, y: Float, pointer: Int, payload: DragAndDrop.Payload?,target: DragAndDrop.Target?) {
                super.dragStop(event, x, y, pointer, payload, target)
                dragging = false
                val node = payload?.`object` as TreeNode<*, *>?
                layout()
                state.hovered = null
                node?.let {
                    node.dragging = false
                }
            }


        })
        dnd.addTarget(object : DragAndDrop.Target(this) {
            override fun drag(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?, x: Float, y: Float, pointer: Int): Boolean {
                val node = payload?.`object` as TreeNode<FileHandle, FileTable>? ?: return false
                if (node.actor.stage == null) return false
                val target = getNodeAt(y)

                if (target == null) {
                    state.hovered?.hovered = false
                    state.hovered = null
                    return false
                }

                val targetParent = target.parent ?: root
                if (target.actor == node.actor) return false
                if (targetParent == node) return false

                if (state.hovered != null && state.hovered != target) {
                    state.hovered!!.hovered = false
                    state.hovered = target
                }

                if (target != node) {
                    selection.set(target)
                    node.actor.setOrigin(node.actor.x,node.actor.y)
                    val targetIndex = targetParent.children.indexOf(target)
                    state.hovered = target
                    state.hoveredInitial = target to targetIndex

                    target.actor.title.color.set(0.5f, 1f, 0.5f, 1f)
                    target.actor.title.setText("Drop here")

                    node.parent?.let { parent ->
                        if(parent == target.parent) {
                            val overindex = parent.children.indexOf(node)
                            val insertionIndex = max(if (targetIndex > overindex) targetIndex else targetIndex - 1,0)

                        }

                        dynamicRemoveReinsert(node, target)
                    }
                }

                if (verifyDropIntegrity(node, target, root)) {
                    restore()
                    return true
                } else
                return restore()
            }

            override fun reset(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?) {
                payload?.dragActor?.let { nodeActor ->
                    nodeActor.addAction(Actions.sequence(fadeOut(2f), Actions.removeActor()))
                }
            }

            override fun drop(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?, x: Float, y: Float, pointer: Int) {
                val node = payload?.`object` as TreeNode<FileHandle, FileTable>
                val target = getNodeAt(y)
                if (target != null) {
                    node.parent?.remove(node)
                    val index = target.parent.children.indexOf(target)
                    target.add(node)
                    info("Dropped ${node.obj.name()} on ${target.obj.name()}")
                    layout()
                }
            }
        })

        onInterfaceInitialized {
            dnd.addTarget(object : DragAndDrop.Target(UI.root){
                override fun drag(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?, x: Float, y: Float, pointer: Int): Boolean {
                    return true
                }
                override fun drop(source: DragAndDrop.Source?, payload: DragAndDrop.Payload?, x: Float, y: Float, pointer: Int) {

                }
            })
        }


    }


    fun dynamicRemoveReinsert(dragging: FNode, over: FNode) {

        if (!checkValidInsertion(dragging,over)) return

        val index = over.parent.children.indexOf(over)
//       insert the dragged node at the index of the node we're hovering over.
        over.parent.remove(dragging)
        over.parent.insert(index,dragging)
//        then, reinsert the hovered node at the correct index, displacing it by one
        over.parent.remove(over)
//        the insertion depends on the position of the dragged node
        if (index > over.parent.children.indexOf(dragging)) {
            over.parent.insert(index-1,over)
        } else if (index > -1){
            over.parent.insert(index,over)
        }
    }

    fun checkValidInsertion(dragging: FNode, over: FNode) : Boolean {
        val index = over.parent.children.indexOf(over)
        val parent = over.parent
        val children = parent.children
        val draggedIndex = children.indexOf(dragging)
        return if (draggedIndex < index) {
            children[draggedIndex+1] != over
        } else {
            children[draggedIndex-1] != over
        }
    }

    fun restore() : Boolean{
        state.hoveredInitial?.let { (node, index) ->
            node.actor.title.color.set(1f, 1f, 1f, 1f)
            node.actor.title.setText(node.obj.name())
        }
        state.draggedInitial?.let { (node, index) ->
            if (state.dragged != null) {
                node.insert(index, state.dragged!!)
            }
        }
        return false
    }

    override fun drawBackground(batch: Batch?, parentAlpha: Float) {
        super.drawBackground(batch, parentAlpha)
    }

    override fun handleSelection(selection: OrderedSet<TreeNode<FileHandle,FileTable>>) {
        info("Selected ${selection.size} items")
    }


    inner class FileNode(val file: FileHandle) : TreeNode<FileHandle, FileTable>(file.name(),file,
        FileTable(file, parentRef ?: this@FileTree)
    ) {

        init {
            actor = FileTable(file,parentRef ?: this@FileTree).apply {
                node = this@FileNode
            }
            isExpanded = false
        }

        override fun setExpanded(expanded: Boolean) {
            super.setExpanded(expanded)
        }
    }

    inner class FileTable(val file: FileHandle, sizingactor: Actor) : TreeActor<FileHandle>(file) {

        override var title: SLabel = SLabel(file.name())

        var tableactors : STable = STable()



        override fun buildActor(obj: FileHandle) {



            val imageIcon = resolveIcon(obj)
            icon = imageIcon
            val image = SImage(icon!!)
            val label : SLabel = SLabel(obj.name())
            title = label
            iconImage = image
            label.setEllipsis(true)
            label.touchable = Touchable.disabled
            label.wrap = true


            overflow.pop {
                val label = SLabel("Open")
                add(label).row()
                label.onClick {
                    FileOpener.open(file.file())
                }
            }

            child { table ->
                add(iconImage).size(18f).padRight(4f)
                add(title).growX()
                tableactors = table
            }.growX()

            add(overflow).size(18f).padLeft(4f)




            align(Align.left)
        }

        fun resolveIcon(obj : FileHandle) : Drawable {
            val ext = obj.extension() ?: "file"
            return if (obj.isDirectory) UI.drawable("default-folder")
            else when (ext) {
                "png" ->  UI.drawable("image_icon")
                "scene" -> UI.drawable("file_scene")
                "salient" -> UI.drawable("logo16")
                "gltf" -> UI.drawable("model_file")
                "obj" -> UI.drawable("model_file")
                else -> UI.drawable("file")
            }
        }

        override fun drawBackground(batch: Batch, parentAlpha: Float, x: Float, y: Float) {
            val over = getOverNode() ?: return
            if(over.actor == this && over.hovered) {
                batch.color = Color.WHITE.cpy().alpha(0.5f)
                batch.draw(pixel, x, y, width, 1f)

            }
        }

        override fun getPrefWidth(): Float {

        val leftPaneW = UI.root.left.container.width - 5
        return min(this@FileTree.width - x - 5, UI.root.left.container.width)
    }
    }

    companion object {
        private val tmp = Vector2()
        private var dragActor: Actor? = null
        private var tmpParent: TreeNode<FileHandle, FileTree.FileTable>? = null
        private var originalSelection : TreeNode<FileHandle, FileTree.FileTable>? = null
        var initialState : Pair<TreeNode<FileHandle, FileTree.FileTable>,Int>? = null

        val folder_collapsed = { UI.drawable("default-folder") }
        val folder_expanded = { UI.drawable("default-folder-open") }

        fun verifyDropIntegrity(source: TreeNode<FileHandle, FileTable>, target: TreeNode<FileHandle, FileTable>, root : TreeNode<FileHandle, FileTable>): Boolean {
            val sourceFile = source.obj
            val targetFile = target.obj
            if (sourceFile == targetFile) return false
            if (sourceFile.parent() == targetFile) return false
            if (sourceFile.isDirectory && targetFile.isDirectory) return true
            if (sourceFile.isDirectory && !targetFile.isDirectory) return false
            if (!sourceFile.isDirectory && !targetFile.isDirectory) return false

            val depthSource = calculateDepthFromRoot(source, root)


            return true
        }

        fun calculateDepthFromRoot(node: TreeNode<*, *>, root: TreeNode<*, *>): Int {
            var depth = 0
            var current = node
            while (current != root) {
                current = current.parent!!
                depth++
            }
            return depth
        }

    }



}